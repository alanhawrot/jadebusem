# coding=utf-8
import datetime
import traceback

from django.core.exceptions import PermissionDenied, ObjectDoesNotExist, ValidationError
from django.db import transaction
from django.http import HttpResponseRedirect
from django.utils.translation import ugettext as _
import Users.views
from models import *


__author__ = 'michal'
from django.shortcuts import render


def list_all(request):
    if 'email' in request.session:
        author = request.session.get('user_id')
        user = JadeBusemUser.objects.get(user_id=author)
        topics = []
        for contributed_topic in Contributor.objects.filter(contributor=user):
            topic = contributed_topic.topic
            new_messages = False
            last_date = topic.messages.all().order_by("-date")[0].date
            try:
                last_topic_check = LastTopicCheck.objects.get(topic=topic, user=user)
                for message in topic.messages.all():
                    if message.date >= last_topic_check.last_check:
                        new_messages = True
            except ObjectDoesNotExist:
                new_messages = True
            topics.append((topic, new_messages, last_date))
        topics = sorted(topics, key=lambda _tuple: _tuple[2], reverse=True)
        return render(request, 'topics/list_all.html', {'topics': topics, 'login': True, 'user': request.session})
    else:
        return Users.views.sign_in(request)


def show(request, topic, context={}):
    if 'email' in request.session:
        author = request.session.get('user_id')
        user = JadeBusemUser.objects.get(user_id=author)
        if not Contributor.objects.filter(contributor=user, topic__id=topic):
            raise PermissionDenied()

        topic = Topic.objects.get(pk=topic)
        messages = topic.messages.all().order_by("date")

        try:
            last_topic_check = LastTopicCheck.objects.get(topic=topic, user=user)
            last_topic_check.last_check = datetime.datetime.now()
            last_topic_check.full_clean()
            last_topic_check.save()
        except ObjectDoesNotExist:
            last_topic_check = LastTopicCheck(topic=topic, user=user)
            last_topic_check.last_check = datetime.datetime.now()
            last_topic_check.full_clean()
            last_topic_check.save()
        return render(request, 'topics/show.html',
                      dict(context.items() + {'topic': topic, 'messages': messages,
                                              'login': True, 'user': request.session}.items()))
    else:
        return Users.views.sign_in(request)


def create(request, schedule, context={}):
    if 'email' in request.session:
        author = request.session.get('user_id')
        user = JadeBusemUser.objects.get(user_id=author)
        schedule = Schedule.objects.get(pk=schedule)
        invited_users = []
        if user != schedule.author:
            invited_users.append(schedule.author)
        if request.POST:
            topic, errors = handle_create_topic(user, invited_users, schedule,
                                                request.POST.get('topic'),
                                                request.POST.get('message'))
            if not errors:
                return HttpResponseRedirect("/topics/show/{}".format(topic.id))
            else:
                return render(request, 'topics/create.html',
                              dict(context.items() + {
                                  'invite_admin': request.POST.get('invite_admin'),
                                  'topic': request.POST.get('topic'),
                                  'message': request.POST.get('message'),
                                  'schedule': schedule, 'errors': errors, 'login': True, 'user': request.session
                              }.items()))
        else:
            return render(request, 'topics/create.html',
                          {'schedule': schedule, 'login': True, 'user': request.session})
    else:
        return Users.views.sign_in(request)


@transaction.commit_manually
def handle_create_topic(inviting, invited_users, schedule, message_topic, message):
    try:
        errors = {}
        message_topic = unicode(message_topic).strip()
        message = unicode(message).strip()
        try:
            topic = Topic(schedule=schedule, topic=message_topic)
            topic.full_clean()
            topic.save()
        except ValidationError as e:
            errors.update(e.message_dict)
        try:
            contributor = Contributor(topic=topic, contributor=inviting)
            contributor.full_clean()
            contributor.save()
        except ValidationError as e:
            errors.update(e.message_dict)

        for invited_user in invited_users:
            try:
                contributor = Contributor(topic=topic, contributor=invited_user)
                contributor.full_clean()
                contributor.save()
            except ValidationError as e:
                errors.update(e.message_dict)
        try:
            message_model = Message(topic=topic, message=message, author=inviting)
            message_model.full_clean()
            message_model.save()
        except ValidationError as e:
            errors.update(e.message_dict)
        try:
            last_check = LastTopicCheck(topic=topic, user=inviting)
            last_check.full_clean()
            last_check.save()
        except ValidationError as e:
            errors.update(e.message_dict)
        if errors:
            transaction.rollback()
        else:
            transaction.commit()
        return topic, errors
    except Exception as e:
        transaction.rollback()
        traceback.print_exc()
        raise e


def reply(request, topic):
    if 'email' in request.session:
        if request.POST:
            author = request.session.get('user_id')
            user = JadeBusemUser.objects.get(user_id=author)
            topic = Topic.objects.get(pk=topic)
            if topic.closed:
                raise PermissionDenied(_("Illegal access attempt"))
            try:
                Contributor.objects.get(contributor=user, topic=topic)
                message = Message(topic=topic, author=user)
                message.message = request.POST.get('message').strip()
                message.full_clean()
                message.save()
                return HttpResponseRedirect("/topics/show/{}".format(topic.id))
            except ObjectDoesNotExist:
                raise PermissionDenied(_("Illegal access attempt"))
            except ValidationError as e:
                return show(request, topic.id, {'errors': e.message_dict, 'message': request.POST.get('message')})
        raise PermissionDenied(_("Illegal access attempt"))
    else:
        return Users.views.sign_in(request)


def close(request, topic):
    if 'email' in request.session:
        author = request.session.get('user_id')
        user = JadeBusemUser.objects.get(user_id=author)
        topic = Topic.objects.get(pk=topic)
        try:
            Contributor.objects.get(contributor=user, topic=topic)
            if True:
                #TODO: need to add permission logic closing topic
                topic.closed = True
                topic.save()
                return list_all(request)
            else:
                raise PermissionDenied(_("Illegal access attempt"))
        except ObjectDoesNotExist:
            raise PermissionDenied(_("Illegal access attempt"))
    else:
        return Users.views.sign_in(request)
