# coding=utf-8
from django.shortcuts import render
from django.template import Context
import Users
from schedules import scheduleBuilder
from schedules.scheduleBuilder import ScheduleBuilder


def add(request):
    author = request.session.get('user_id')
    if not author:
        return Users.views.sign_in()
    if 'email' in request.session:
        context = {'mainheader': 'Dodaj rozkład',
                   'form1': {'show': True,
                             'header': "Wyślij zdjęcie"},
                   'form2': {'show': True,
                             'header': "Uzupełnij Formularz"},
                   'login': True,
                   'user': request.session}
        schedule = None
    else:
        context = {'mainheader': 'Dodaj rozkład',
                   'form1': {'show': True,
                             'header': "Wyślij zdjęcie"},
                   'form2': {'show': True,
                             'header': "Uzupełnij Formularz"}}

    image = request.FILES.get('imageUpload')
    company = request.POST.get('companyName','')
    author = request.session.get('user_id')
    trace_points = request.POST.getlist('tracePoint',[''])

    schedule = ScheduleBuilder(company=company, author=author, image=image)

    if request.FILES:
        context['mainheader'] = "Weryfikacja danych"
        context['form1']['show'] = False
        context['form2']['header'] = ''
    elif request.POST:
        schedule.parse_hours(request.POST)
        schedule.parse_trace_points(trace_points)
        scheduleModel = schedule.build_model()
        #scheduleModel.scheduletracepoint_set().save()
        #scheduleModel.scheduledate_set().save()
        context = {'mainheader': 'Pomyślnie dodano rozkład!',
               'form1': {'show': False,
                         'header': ""},
               'form2': {'show': False,
                         'header': ""}}
    context['schedule'] = schedule
    return render(request, 'schedules/add.html', Context(context))






