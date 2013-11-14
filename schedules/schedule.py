__author__ = 'michal'

class Schedule(object):
    def __init__(self):


        self.company = ""
        self.trace_points = []
        self.days = []
        for i in xrange(7):
            self.days.append([])

def parse_schedule_from_image(image):
    """


    @param image:
    @return:
    """
    return create_empty_viewable_schedule()


def parse_schedule_from_post(post):
    _schedule = Schedule()
    print post
    _schedule.company = post.get('companyName', '')
    _schedule.trace_points = post.getlist('tracePoint',[''])
    if len(_schedule.trace_points) > 1:
        _schedule.trace_points = [x for x in _schedule.trace_points if x != '']
    for day in xrange(7):
        hours = post.getlist('hour['+str(day)+']', [''])
        hours = [x for x in hours if x != ''] + ['']
        _schedule.days[day].extend(hours)
    print _schedule.days
    return _schedule


def create_empty_viewable_schedule():
    _schedule = Schedule()
    _schedule.company = ''
    _schedule.trace_points.append('')
    for i in xrange(7):
        _schedule.days[i].append('')
    return _schedule