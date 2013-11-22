from django.shortcuts import render_to_response
from schedules.models import Schedule
from schedules.models import ScheduleTracePoint
from schedules.models import ScheduleDate
from django.db.models import Q

def display_schedules(schedules, dates, trace_points, list_of_tabs):
        for schedule in schedules:
            list_of_schedule = create_schedule(schedule, dates, trace_points)
            list_of_tabs.extend([list_of_schedule])
        return list_of_tabs

def create_schedule(schedule, dates, trace_points):

        list_of_schedule = []
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
            number_of_rows = int(max_time[0])-int(min_time[0])+1
            tab =[[ None ]*8 for i in range(number_of_rows)]

            # hour = earliest hour in this schedule
            hour = int(min_time[0])

            for i in range(number_of_rows):
                tab[i][0] = hour
                hour += 1

            for time_row in time_in_shedule:
                time_tab = str(time_row.time).split(":")

                # Get the row of table, where time_tab[1] will be placed. time_tab[1] - minutes
                row = int(time_tab[0])-int(min_time[0])

                column = time_row.day
                column = (int(column)+1)

                try:
                    # add minutes to the empty cell in table
                    if tab[row][column] == None:
                        tab[row][column] = time_tab[1]
                    else:
                        # if cell is not empty than add , and value
                        tab[row][column] += ", "
                        tab[row][column] += time_tab[1]
                except:
                    error = "Blad podczas tworzenia rozkladu"

            trace_in_schedule = trace_points.filter(schedule_id=schedule.id)

            trace = ""
            for trace_point in trace_in_schedule:
                if trace == "":
                    trace = trace_point.address
                else:
                    trace += " -> " + trace_point.address
            list_of_schedule.extend([schedule.id, schedule.company, schedule.author, trace])
            list_of_schedule.extend([tab])
        except:
            error = "Blad podczas tworzenia rozkladu"
        return list_of_schedule

def search(request):
    from_to_error = ""
    login = False
    list_of_tabs = []
    trace_id = ""

    # session
    if 'email' in request.session:
        login = True

    # search engine
    if request.method == 'POST':
        if(request.POST['company_name'] == "Wszystkie"):
            schedules = Schedule.objects.all()
            trace_points = ScheduleTracePoint.objects.all()
            dates = ScheduleDate.objects.all()
        else:
            schedules = Schedule.objects.filter(company=request.POST['company_name'])
            trace_points = ScheduleTracePoint.objects.filter(schedule_id__in=schedules)
            dates = ScheduleDate.objects.filter(schedule_id__in=schedules)

        if(request.POST['from'] != ""):
            trace_id = trace_points.filter(address=request.POST['from']).values("schedule_id")
            schedules = schedules.filter(id__in=trace_id)
            trace_points = trace_points.filter(schedule_id__in=schedules)

            if(request.POST['to'] != ""):
                trace_id = trace_points.filter(address=request.POST['to']).values("schedule_id")
                schedules = schedules.filter(id__in=trace_id)
                trace_points = trace_points.filter(schedule_id__in=schedules)
            dates = dates.filter(schedule_id__in=schedules)

        elif(request.POST['to'] != ""):
            from_to_error = "Brak miejsca poczatkowego"

        list_of_tabs = display_schedules(schedules, dates, trace_points, list_of_tabs)

    companies = Schedule.objects.values('company').distinct()

    return render_to_response('search_engine/search.html', {
                                                            'login': login,
                                                            'user': request.session,
                                                            'from_to_error': from_to_error,
                                                            'companies': companies,
                                                            'list_of_tabs': list_of_tabs
                                                            })