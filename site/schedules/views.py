# coding=utf-8
import json
import time

from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.http.response import HttpResponseForbidden, HttpResponseNotFound, HttpResponse, HttpResponseRedirect
from django.shortcuts import render
from django.template import Context
from django.utils.translation import ugettext as _
from django.core.exceptions import ObjectDoesNotExist, ValidationError
from django.db import transaction
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.views import APIView

from Users.models import JadeBusemUser
from Users.views import sign_in
from schedules.models import ScheduleDate, ScheduleTracePoint
from models import Schedule
from schedules.serializers import PaginatedScheduleSerializer
from schedules_settings import ScheduleAccessManager


def show_list(request):
    author = request.session.get('user_id')
    user = JadeBusemUser.objects.get(user_id=author)
    if not author:
        return sign_in(request)
    context = {
        'schedules': Schedule.objects.filter(author=user),
        'user': user
    }

    if 'email' in request.session:
        context['login'] = True,
        context['user'] = request.session

    return render(request, 'schedules/show_list.html', Context(context))


def add(request):
    author = request.session.get('user_id')
    user = JadeBusemUser.objects.get(user_id=author)

    if not author:
        return sign_in(request)
    schedule_id = None
    if request.POST:
        schedule_id = request.POST.get('schedule_id')
        print schedule_id
    s = Schedule(id=schedule_id, author=user)
    return schedule(request, user, s)


def modify(request, schedule_id):
    author = request.session.get('user_id')
    user = JadeBusemUser.objects.get(user_id=author)

    if not author:
        return sign_in(request)
    s = Schedule.objects.get(id=schedule_id)
    return schedule(request, user, s)


def delete(request, schedule_id):
    author = request.session.get('user_id')
    user = JadeBusemUser.objects.get(user_id=author)

    if not author:
        return sign_in(request)
    s = Schedule.objects.get(id=schedule_id)
    manager = ScheduleAccessManager()
    manager.schedule = s
    manager.user = user
    if manager.can_delete():
        s.delete()
    return HttpResponseRedirect("/schedules/show_list")


def schedule(request, user, _schedule):
    context = {'user': request.session, 'login': True}
    m = ScheduleAccessManager()
    m.user = user
    m.schedule = _schedule
    schedule_id = _schedule.id
    try:
        if not schedule_id or schedule_id and m.can_write():
            if request.is_ajax():
                context['errors'] = errors = {}
                data = json.loads(request.body)
                handle_schedule_change_request(data, errors, _schedule)
                if not errors:
                    context['success'] = True
                    if schedule_id:
                        context['message'] = _("Successfully modified schedule.")
                    else:
                        context['message'] = _("Successfully added schedule.")
                else:
                    context['success'] = False
                del context['user']
                context['schedule_id'] = _schedule.id
                if not schedule_id and not errors:
                    context['redirect'] = '/schedules/modify/{}'.format(_schedule.id)
                return HttpResponse(json.dumps(context), content_type="application/json")
            context['schedule_id'] = _schedule.id
            s = {'company': _schedule.company, 'days': [], 'trace_points': [],
                 'id': _schedule.id}

            for trace_point in _schedule.trace_points.all().order_by('position'):
                s['trace_points'].append({'address': trace_point.address, 'id': trace_point.position})
            for i in xrange(7):
                day = []
                for departure in ScheduleDate.objects.filter(schedule=_schedule, day=i):
                    day.append({'id': departure.id, 'hour': departure.time})
                s['days'].append(day)
            context['schedule'] = s
            return render(request, 'schedules/schedule.html', Context(context))
        else:
            return HttpResponseForbidden()
    except ObjectDoesNotExist:
        return HttpResponseNotFound()


@transaction.commit_manually
def handle_schedule_change_request(data, errors, _schedule):
    try:
        if _schedule.id:
            _schedule.trace_points.all().delete()
            _schedule.departures.all().delete()
        _schedule.company = data['company_name']
        _schedule.save()
        if not data['trace_points'] or len(data['trace_points']) < 2:
            errors[_('trace points error ')] = _("At least two trace points are needed.")
        for trace_point in data['trace_points']:
            tp = ScheduleTracePoint(schedule_id=_schedule.id,
                                    position=trace_point.get('id'),
                                    address=trace_point.get('address'))
            try:
                tp.full_clean()
                tp.save()
            except ValidationError as e:
                errors.update(e.message_dict)

        no_hours = True
        for day_num, day in enumerate(data['days']):
            for departure in day:
                try:
                    hour = departure.get('hour')
                    try:
                        departure['hour'] = time.strptime(hour, "%H:%M")
                    except BaseException as e:
                        print e
                    if hour:
                        no_hours = False
                    sd_model = ScheduleDate(schedule_id=_schedule.id, time=hour, day=day_num)
                    sd_model.full_clean()
                    sd_model.save()
                except ValidationError as e:
                    errors.update(e.message_dict)
        if no_hours:
            errors[_('departures error')] = _("Need to add at least one departure.")

        try:
            _schedule.full_clean()
            _schedule.save()
        except ValidationError as e:
            errors.update(e.message_dict)

        if errors:
            transaction.rollback()
        else:
            transaction.commit()
    except BaseException as E:
        transaction.rollback()
        print E


@api_view(['GET'])
def get_all_schedules(request, page):
    queryset = Schedule.objects.all()
    paginator = Paginator(queryset, 5)
    try:
        schedules = paginator.page(page)
    except PageNotAnInteger:
        schedules = paginator.page(1)
    except EmptyPage:
        schedules = paginator.page(paginator.num_pages)
    serializer_context = {'request': request}
    serializer = PaginatedScheduleSerializer(schedules, context=serializer_context)
    return Response(serializer.data)

      
@api_view(['POST'])
def handle_schedule(request):
    data = json.loads(request.body)
    author_of_schedule = JadeBusemUser.objects.get(email=data['email'])
    if not data['schedule_id']:
        schedule = Schedule(id=None, author=author_of_schedule)
    else
        schedule = Schedule(id=data['schedule_id'], author=author_of_schedule)
    errors = {}
    handle_schedule_change_request(data, errors, schedule)
    return Response(content_type='application/json')