# coding=utf-8
from django.shortcuts import render
from django.template import Context
from Users.views import sign_in
from schedules.scheduleBuilder import ScheduleBuilder


def add(request):
    author = request.session.get('user_id')
    if not author:
        return sign_in(request)
    context = {
        'mainheader': 'Dodaj rozkład',
        'form1': {
            'show': True,
            'header': "Wyślij zdjęcie"},
        'form2': {
            'show': True,
            'header': "Uzupełnij Formularz"}}

    if 'email' in request.session:
        context['login'] = True,
        context['user'] = request.session

    image = request.FILES.get('imageUpload')
    company = request.POST.get('companyName', '')
    author = request.session.get('user_id')
    trace_points = request.POST.getlist('tracePoint', [''])

    scheduleBuilder = ScheduleBuilder(company=company, author=author, image=image)

    if request.FILES:
        context['mainheader'] = "Weryfikacja danych"
        context['form1']['show'] = False
        context['form2']['header'] = ''
        context['schedule'] = scheduleBuilder
    elif request.POST:
        scheduleBuilder.parse_hours(request.POST)
        scheduleBuilder.parse_trace_points(trace_points)
        scheduleModel = scheduleBuilder.build_model()
        #TODO:handle errors
        context['mainheader'] = 'Pomyślnie dodano rozkład!'
        context['schedule'] = ScheduleBuilder(author=author)
    else:
        context['schedule'] = scheduleBuilder
    return render(request, 'schedules/add.html', Context(context))






