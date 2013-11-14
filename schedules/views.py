# coding=utf-8
from django.shortcuts import render
from django.template import Context
from schedules import schedule
from schedules.schedule import parse_schedule_from_post, create_empty_viewable_schedule, parse_schedule_from_image


def add(request):
    if 'email' in request.session:
        context = { 'mainheader' : 'Dodaj rozkład',
                    'form1': {'show': True,
                                'header': "Wyślij zdjęcie"},
                    'form2': {'show': True,
                                'header': "Uzupełnij Formularz"},
                    'login': True,
                    'user': request.session}
        _schedule = None
    else:
        context = { 'mainheader' : 'Dodaj rozkład',
                    'form1': {'show': True,
                                'header': "Wyślij zdjęcie"},
                    'form2': {'show': True,
                                'header': "Uzupełnij Formularz"}}
        _schedule = None
    if request.FILES:
        image = request.FILES.get('imageUpload', '')
        _schedule = parse_schedule_from_image(image)
        _schedule.company = request.POST.get('companyName', '')
        context['form1']['show'] = False
        context['mainheader'] = "Weryfikacja danych"
        context['form2']['header'] = ''
    elif request.POST:
        _schedule = parse_schedule_from_post(request.POST)
    else:
        _schedule = create_empty_viewable_schedule()

    context['schedule'] = _schedule
    return render(request, 'schedules/add.html', Context(context))






