from Users.models import JadeBusemUser

__author__ = 'michal'
from models import Schedule, ScheduleTracePoint, ScheduleDate


class ScheduleBuilder(object):
    def __init__(self, author, company='', image=None):
        self.image = image
        self.author = author
        self.company = company
        self.trace_points = ['']
        self.days = []
        for i in xrange(7):
            self.days.append([''])

    def build_model(self):
        user = JadeBusemUser.objects.get(user_id=self.author)
        schedule_model = Schedule(author=user, company=self.company, image_path=self.image)
        schedule_model.save()
        for day in xrange(len(self.days)):
            for hour in self.days[day]:
                if hour:
                    date_model = ScheduleDate(schedule=schedule_model, day=day, time=hour)
                    schedule_model.departures.add(date_model)
        for trace_point in self.trace_points:
            if trace_point:
                trace_point_model = ScheduleTracePoint(schedule=schedule_model, address=trace_point)
                schedule_model.trace_points.add(trace_point_model)
        return schedule_model

    def parse_hours(self, POST):
        for day in xrange(7):
            hours = POST.getlist('hour[' + str(day) + ']', [''])
            hours = [x for x in hours if x != ''] + ['']
            self.days[day] = hours

    def parse_trace_points(self, trace_points):
        if len(trace_points) > 1:
            self.trace_points = [x for x in trace_points if x != '']
        else:
            self.trace_points = ['']
