from django.db.models.fields.related import ForeignKey
from Users.models import JadeBusemUser
from schedules.models import Schedule

__author__ = 'michal'
from django.db import models
from django.utils.translation import ugettext as _


class Topic(models.Model):
    topic = models.CharField(_('Topic'), blank=False, null=False, max_length=200)
    closed = models.BooleanField(_("Is closed"), default=False)
    schedule = models.ForeignKey(Schedule, related_name="topics")


class Contributor(models.Model):
    topic = models.ForeignKey(Topic)
    contributor = models.ForeignKey(JadeBusemUser)


class LastTopicCheck(models.Model):
    topic = ForeignKey(Topic, related_name="last_check")
    user = ForeignKey(JadeBusemUser, related_name="last_check")
    last_check = models.DateTimeField(_("Last check"), auto_now=True)


class Message(models.Model):
    topic = models.ForeignKey(Topic, related_name="messages")
    author = models.ForeignKey(JadeBusemUser, related_name="author")
    message = models.TextField(blank=False, null=False)
    date = models.DateTimeField(_("Date"), auto_now=True)
