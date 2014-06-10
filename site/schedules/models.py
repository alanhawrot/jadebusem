# coding=utf-8
import os
from django.db import models
from Users.models import JadeBusemUser
from django.utils.translation import ugettext as _


def upload_url(self, instance, filename):
    return os.join('http://127.0.0.1:8000/static/schedule_images/', filename)


class Schedule(models.Model):
    author =        models.ForeignKey(JadeBusemUser)
    company =       models.CharField(_('Company name'), max_length=200, blank=False, null=False)
    image_path =    models.FileField(_("Image path"), upload_to=upload_url, blank=True, null=True)
    verified =      models.BooleanField(_("Is verified"), default=False)

    def __unicode__(self):
        return _(u"Author: ") + unicode(self.author) + _(u", Company name: ") + unicode(self.company)


class ScheduleTracePoint(models.Model):
    schedule =      models.ForeignKey(Schedule, related_name='trace_points')
    address =       models.CharField(_("Address"), max_length=200)
    position =      models.IntegerField()

    def __unicode__(self):
        return unicode(self.address)


class ScheduleDate(models.Model):
    days = [(0, _(u'Poniedziałek')),
            (1, _(u'Wtorek')),
            (2, _(u'Środa')),
            (3, _(u'Czwartek')),
            (4, _(u'Piątek')),
            (5, _(u'Sobota')),
            (6, _(u'Niedziela'))]

    schedule =      models.ForeignKey(Schedule, related_name='departures')
    time =          models.TimeField(_("Departure time"))
    day =           models.PositiveSmallIntegerField(choices=days)

    def __unicode__(self):
        return unicode(self.time) + unicode(self.day)