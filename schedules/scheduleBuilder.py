__author__ = 'michal'
from models import Schedule

class ScheduleBuilder(object):
    def __init__(self,company,image,author,trace_points):

        self.image=image
        self.author=author
        self.company = company
        self.trace_points = ['']
        self.days = []
        for i in xrange(7):
            self.days.append([])

    def build_model(self):
        pass#model = Schedule(author=,company=comany)

    def parse_hours(self, POST):
        for day in xrange(7):
            hours = POST.getlist('hour['+str(day)+']', [''])
            hours = [x for x in hours if x != ''] + ['']
            self.days[day].extend(hours)

    def parse_trace_points(self, trace_points):
        if len(trace_points) > 1:
            self.trace_points = [x for x in trace_points if x != '']
        else:
            self.trace_points = ['']
