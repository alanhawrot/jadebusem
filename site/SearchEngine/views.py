# -*- coding: utf-8 -*-

from django.shortcuts import render_to_response, render
from django.template import RequestContext, Context
from schedules.models import Schedule
from schedules.models import ScheduleTracePoint
from schedules.models import ScheduleDate
from django.utils.translation import ugettext as _

trace = ""

#-------------------------------------------------------------------------------------
# Function to translate polish chars to ascii
#-------------------------------------------------------------------------------------

def plToAng(text):

    translate = {u'ą':'a', u'ć':'c', u'ę':'e', u'ł':'l', u'ń':'n', u'ó':'o', u'ś':'s', u'ż':'z', u'ź':'z'}

    newText = ''
    for c in text:
        if c in translate:
            c = translate[c]
        newText = newText + c
    return newText

#-------------------------------------------------------------------------------------
# Function to display schedules
#-------------------------------------------------------------------------------------
def display_schedules(schedules, dates, trace_points, list_of_tabs, route_list):
    temp_list = []
    for schedule in schedules:
        dic = create_schedule(schedule, dates, trace_points, "", "")
        list_of_schedule = dic['list_of_schedule']
        trace = dic['trace']
        list_of_tabs.extend([[list_of_schedule]])
        route_list.extend([trace])
    return list_of_tabs

#-------------------------------------------------------------------------------------
# Function to create timetable in schedules
#-------------------------------------------------------------------------------------
def create_schedule(schedule, dates, trace_points, start, stop):
    list_of_schedule = []
    dic = {'list_of_schedule': [], 'trace': ""}
    try:
        # Get only this hours, whose are in this schedule
        time_in_shedule = dates.filter(schedule_id=schedule.id)

        # Get the the earliest and the latest time in schedule
        min_time = time_in_shedule.order_by('time')[0]
        max_time = time_in_shedule.order_by('-time')[0]

        # Get arrays of [hours,minutes,seconds]
        min_time = str(min_time.time).split(":")
        max_time = str(max_time.time).split(":")

        # Calculate the amount of rows in schedule e.g min_time = 12, max_time = 15 then our table on webstie will have 4 rows
        number_of_rows = int(max_time[0]) - int(min_time[0]) + 1
        tab = [[None] * 8 for i in range(number_of_rows)]

        # hour = earliest hour in this schedule
        hour = int(min_time[0])

        for i in range(number_of_rows):
            tab[i][0] = hour
            hour += 1

        for time_row in time_in_shedule:
            time_tab = str(time_row.time).split(":")

            # Get the row of table, where time_tab[1] will be placed. time_tab[1] - minutes
            row = int(time_tab[0]) - int(min_time[0])

            column = time_row.day
            column = (int(column) + 1)

            try:
                # add minutes to the empty cell in table
                if tab[row][column] == None:
                    tab[row][column] = time_tab[1]
                else:
                    # if cell is not empty than add , and value
                    tab[row][column] += ", "
                    tab[row][column] += time_tab[1]
            except:
                error = _("Error while creating schedule")

        trace_in_schedule = trace_points.filter(schedule_id=schedule.id)

        trace = ""
        trace_temp = ""
        if start != "":
            flag = False
            flag_temp = False
        else:
            flag = True
            flag_temp = False
        for trace_point in trace_in_schedule:
            if flag:
                flag_temp = True
            if start == trace_point.address:
                flag = True
            if flag:
                if trace == "":
                    trace = trace_point.address
                else:
                    trace += " -> " + trace_point.address

                if flag_temp:
                    if trace_temp == "":
                        trace_temp = trace_point.address
                    else:
                        trace_temp += " -> " + trace_point.address
                if stop == trace_point.address:
                    break
        list_of_schedule.extend([schedule.id, schedule.company, schedule.author, trace])
        list_of_schedule.extend([tab])
        dic['list_of_schedule'] = list_of_schedule
        dic['trace'] = trace_temp
    except:
        error = _("Error while creating schedule")
    return dic

#-------------------------------------------------------------------------------------
# Function to check is bus go in that direction which we want
#-------------------------------------------------------------------------------------
def isGoodDirection(schedules, trace_points, start_address, end_address, exclude_list):
    list = []
    for schedule in schedules:
            isGoodDirectionFrom = trace_points.filter(schedule_id=schedule.id).filter(address__iexact=start_address)[:1]
            isGoodDirectionTo = trace_points.filter(schedule_id=schedule.id).filter(address__iexact=end_address)[:1]

            for isF in isGoodDirectionFrom:
                for isT in isGoodDirectionTo:
                    if(isF.id < isT.id):
                        list.append(schedule.id)
            exclude_list.append(schedule.id)
    return list

#-------------------------------------------------------------------------------------
# Algorithm that find schedules with one interchange
#-------------------------------------------------------------------------------------

def OneInterchange(start_points, exclude_list, start_address, end_address, list_of_tabs, error, route_list):
        start_points_id = start_points.exclude(schedule_id__in=exclude_list).values("schedule").distinct()
        list_of_interchanges = []
        # For each unused schedule, start to search interchanges
        for points in start_points_id:
            list_of_stops = []
            # Get only those tables that have start place
            interchange_points = start_points.filter(schedule_id=points['schedule'])
            interchange_start_point = interchange_points.filter(address__iexact=start_address)
            id = 0
            # set "id" to id of start place in each schedule
            for interchange in interchange_start_point:
                id = interchange.id
            # create a list of stops (id > start point means that bus go in a right direction)
            for interchange in interchange_points:
                if(id < interchange.id):
                    list_of_stops.append(interchange.address)
            # Search for interchanges
            # Get those schedules that doesn't have start point
            interchanges_stops_id = ScheduleTracePoint.objects.filter(address__iexact=start_address).values("schedule_id").distinct()
            interchanges_stops = ScheduleTracePoint.objects.exclude(schedule_id__in=interchanges_stops_id)
            # Get those schedules that contain any stops form list_of_stops -> 1
            interchanges_stops_id = interchanges_stops.filter(address__in=list_of_stops).values("schedule_id").distinct()
            interchanges_stops = ScheduleTracePoint.objects.filter(schedule_id__in=interchanges_stops_id)
            # Get those schedules (from 1) that contain end point
            interchanges_stops_id = interchanges_stops.filter(address__iexact=end_address)

            # Checking is bus driving in good direction
            for inter in interchanges_stops_id:
                isGoodDirectionFrom = interchanges_stops.filter(schedule_id=inter.schedule_id).filter(address__in=list_of_stops).order_by('id')[:1]
                isGoodDirectionTo = interchanges_stops.filter(schedule_id=inter.schedule_id).filter(address__iexact=end_address)[:1]
                for i in isGoodDirectionFrom:
                    for j in isGoodDirectionTo:
                        if i.id < j.id:
                            list_of_interchanges.extend([[points['schedule'], i.address, inter.schedule_id]])
                            exclude_list.append(points['schedule'])
                            exclude_list.append(inter.schedule_id)

        if(len(list_of_interchanges) == 0):
            return error

        # Display shcedules with one interchange
        for id1, stop, id2 in list_of_interchanges:
            schedules = Schedule.objects.filter(id=id1)
            dates = ScheduleDate.objects.filter(schedule_id=id1)
            trace_points = ScheduleTracePoint.objects.filter(schedule_id=id1)
            for schedule in schedules:
                dic = create_schedule(schedule, dates, trace_points, "", stop)
                list_of_schedule = dic['list_of_schedule']
                trace = dic['trace']
            temp = trace + " -> "
            schedules = Schedule.objects.filter(id=id2)
            dates = ScheduleDate.objects.filter(schedule_id=id2)
            trace_points = ScheduleTracePoint.objects.filter(schedule_id=id2)
            for schedule in schedules:
                dic = create_schedule(schedule, dates, trace_points, stop, "")
                list_of_schedule2 = dic['list_of_schedule']
                trace = dic['trace']
            temp = temp + trace
            route_list.extend([temp])
            list_of_tabs.extend([[list_of_schedule, list_of_schedule2]])
        return ""
#-------------------------------------------------------------------------------------
# Algorith that find schedules with two interchanges
#-------------------------------------------------------------------------------------
def TwoInterchanges(start_points, exclude_list, start_address, end_address, list_of_tabs, error, route_list):
    start_points_id = start_points.exclude(schedule_id__in=exclude_list).values("schedule").distinct()
    list_of_end_stops = []
    list_of_interchanges = []

    # For each unused schedule, start to search interchanges
    for points in start_points_id:
        list_of_stops = []
        # Get only those tables that have start place
        interchange_points = start_points.filter(schedule_id=points['schedule'])
        interchange_start_point = interchange_points.filter(address__iexact=start_address)
        id = 0
        # set "id" to id of start place in each schedule
        for interchange in interchange_start_point:
            id = interchange.id
        # create a list of stops (id > start point means that bus go in a right direction)
        for interchange in interchange_points:
            if(id < interchange.id):
                list_of_stops.append(interchange.address)

        # Create a list of stops that cointain end point
        end_points_id = ScheduleTracePoint.objects.exclude(schedule_id__in=exclude_list).exclude(schedule_id__in=start_points_id).filter(address__iexact=end_address).values("schedule").distinct()
        interchange_points = ""
        id = 0
        for i in end_points_id:
            interchange_points = ScheduleTracePoint.objects.filter(schedule_id=i['schedule'])
            interchange_end_point = interchange_points.filter(address__iexact=end_address)
            # set "id" to id of end place in each schedule
            for interchange in interchange_end_point:
                id = interchange.id
            #  create a list of end stops (id < end point means that bus go in a right direction)
            for interchange in interchange_points:
                if id > interchange.id:
                    if interchange.address not in list_of_end_stops:
                        list_of_end_stops.append(interchange.address)

        # Serching for schedules in list_of_stops and list_of_end_stops
        indirect_points_id = ScheduleTracePoint.objects.exclude(schedule_id__in=exclude_list).exclude(schedule_id__in=start_points_id).filter(address__in=list_of_stops).values('schedule').distinct()
        indirect_points_id = ScheduleTracePoint.objects.filter(schedule_id__in=indirect_points_id).filter(address__in=list_of_end_stops).values('schedule').distinct()
        indirect_points = ScheduleTracePoint.objects.filter(schedule_id__in=indirect_points_id)

        # Checking is bus driving in good direction
        for i in indirect_points_id:
            isGoodDirectionFrom = indirect_points.filter(schedule_id=i['schedule']).filter(address__in=list_of_stops).order_by('id')[:1]
            isGoodDirectionTo = indirect_points.filter(schedule_id=i['schedule']).filter(address__in=list_of_end_stops).order_by('id')[:1]
            for j in isGoodDirectionFrom:
                    for k in isGoodDirectionTo:
                        if j.id < k.id:
                            last_part_id = ScheduleTracePoint.objects.exclude(schedule_id__in=exclude_list).filter(address__iexact=k.address).values('schedule').distinct()
                            last_part_id = ScheduleTracePoint.objects.filter(schedule_id__in=last_part_id).filter(address__iexact=end_address).values('schedule').distinct()
                            for l in last_part_id:
                                list_of_interchanges.extend([[points['schedule'], j.address, i['schedule'], k.address, l['schedule']]])

    if(len(list_of_interchanges) == 0):
        return error

    # Display shcedules with two interchanges
    for id1, stop, id2, stop2, id3 in list_of_interchanges:
        schedules = Schedule.objects.filter(id=id1)
        dates = ScheduleDate.objects.filter(schedule_id=id1)
        trace_points = ScheduleTracePoint.objects.filter(schedule_id=id1)
        for schedule in schedules:
            dic = create_schedule(schedule, dates, trace_points, "", stop)
            list_of_schedule = dic['list_of_schedule']
            trace = dic['trace']
        temp = trace + " -> "
        schedules = Schedule.objects.filter(id=id2)
        dates = ScheduleDate.objects.filter(schedule_id=id2)
        trace_points = ScheduleTracePoint.objects.filter(schedule_id=id2)
        for schedule in schedules:
            dic = create_schedule(schedule, dates, trace_points, stop, stop2)
            list_of_schedule2 = dic['list_of_schedule']
            trace = dic['trace']
        temp = temp + trace + " -> "
        schedules = Schedule.objects.filter(id=id3)
        dates = ScheduleDate.objects.filter(schedule_id=id3)
        trace_points = ScheduleTracePoint.objects.filter(schedule_id=id3)
        for schedule in schedules:
            dic = create_schedule(schedule, dates, trace_points, stop2, "")
            list_of_schedule3 = dic['list_of_schedule']
            trace = dic['trace']
        temp = temp + trace
        route_list.extend([temp])
        list_of_tabs.extend([[list_of_schedule, list_of_schedule2, list_of_schedule3]])
    return ""


#-------------------------------------------------------------------------------------
# Main algorithm to search schedules
#-------------------------------------------------------------------------------------


def search(request):
    error = ""
    search_from = ""
    search_to = ""
    login = False
    list_of_tabs = []
    exclude_list = []
    route_list = []

    # session
    if 'email' in request.session:
        login = True

    # search engine
    if request.method == 'POST':
        if(request.POST['from'] != "" and request.POST['to'] != ""):
            start_address = plToAng(request.POST['from'])
            end_address = plToAng(request.POST['to'])

            if (request.POST['company_name'] == _("All")):
                schedules = Schedule.objects.all()
            else:
                schedules = Schedule.objects.filter(company=request.POST['company_name'])

            # Searching for start point
            trace_id = ScheduleTracePoint.objects.filter(schedule_id__in=schedules).filter(address__iexact=start_address).values("schedule_id")
            schedules = schedules.filter(id__in=trace_id)
            start_points = ScheduleTracePoint.objects.filter(schedule_id__in=schedules)

            # Searching for end point
            end_points = start_points.filter(address__iexact=end_address).values("schedule_id")
            schedules = schedules.filter(id__in=end_points)

            # Checking is bus driving in good direction
            list = isGoodDirection(schedules, start_points, start_address, end_address, exclude_list)

            if(len(list) != 0):
                schedules = schedules.filter(id__in=list)
                end_points = start_points.filter(schedule_id__in=list)
                dates = ScheduleDate.objects.filter(schedule_id__in=list)
                list_of_tabs = display_schedules(schedules, dates, end_points, list_of_tabs, route_list)
            else:
                error = _("Sorry, we cant find any schedule.")
                if "interchange" in request.POST.keys():
                    error += _(" Maybe try to search with interchanges.")

            # Searching for interchanges
            if "interchange" not in request.POST.keys():
                error = OneInterchange(start_points, exclude_list, start_address, end_address, list_of_tabs, error, route_list)
                error = TwoInterchanges(start_points, exclude_list, start_address, end_address, list_of_tabs, error, route_list)

            search_from = request.POST['from']
            search_to = request.POST['to']
        else:
            if (request.POST['company_name'] == _("All")):
                schedules = Schedule.objects.all()
            else:
                schedules = Schedule.objects.filter(company=request.POST['company_name'])
            trace_points = ScheduleTracePoint.objects.all()
            dates = ScheduleDate.objects.all()
            list_of_tabs = display_schedules(schedules, dates, trace_points, list_of_tabs, route_list)

    companies = Schedule.objects.values('company').distinct()

    return render(request, 'search_engine/search.html', {
        'login': login,
        'user': request.session,
        'error': error,
        'companies': companies,
        'search_from': search_from,
        'search_to': search_to,
        'list_of_tabs': list_of_tabs,
        'route_list': route_list
    })