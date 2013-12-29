# coding=utf-8
import os
from django.db import models
from Users.models import JadeBusemUser
from jadebusem.settings import MEDIA_URL
from django.utils.translation import ugettext as _

def upload_url(self, instance, filename):
    return os.join('http://127.0.0.1:8000/static/schedule_images/', filename)


class Schedule(models.Model):
    author =        models.ForeignKey(JadeBusemUser)
    company =       models.CharField(_('Company name'), max_length=200, blank=True, null=True)
    image_path =    models.FileField(_("Image path"), upload_to=upload_url, blank=True, null=True)
    verified =      models.BooleanField(_("Is verified"), default=False)

    def __unicode__(self):
        return _(u"Author: ") + unicode(self.author) + _(u", Company name: ") + unicode(self.company)


class ScheduleTracePoint(models.Model):
    schedule =      models.ForeignKey(Schedule)
    address =       models.CharField(_("Address"), max_length=200)


class ScheduleDate(models.Model):
    days = [(0, _(u'Poniedziałek')),
            (1, _(u'Wtorek')),
            (2, _(u'Środa')),
            (3, _(u'Czwartek')),
            (4, _(u'Piątek')),
            (5, _(u'Sobota')),
            (6, _(u'Niedziela'))]

    schedule =      models.ForeignKey(Schedule)
    time =          models.TimeField(_("Departure time"))
    day =           models.PositiveSmallIntegerField(choices=days)