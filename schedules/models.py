import os
from django.db import models
from Users.models import JadeBusemUser
from jadebusem.settings import MEDIA_URL


def upload_url(self, instance, filename):
    return os.join('http://127.0.0.1:8000/static/schedule_images/', filename)

class Schedule(models.Model):
    author = models.ForeignKey(JadeBusemUser)
    company = models.CharField('Company name', max_length=200, blank=True, null=True)


    image_path = models.FileField(
        "Image path",
        upload_to=upload_url,
        blank=True, null=True
    )
    image_path.url = './schedules/'
    verified = models.BooleanField("Is verified", default=False)

    def __unicode__(self):
        return u"Author: " + unicode(self.author)+ u", Company name: " + unicode(self.company)

class ScheduleTracePoint(models.Model):
    schedule = models.ForeignKey(Schedule)
    address = models.CharField("Address", max_length=200)

class ScheduleDate(models.Model):
    schedule = models.ForeignKey(Schedule)
    date = models.DateTimeField("Departure time")