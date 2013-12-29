# coding=utf-8
__author__ = 'michal'
from django import template
from django.utils.translation import ugettext as _

register = template.Library()

@register.filter
def days_of_week(value):
    days = [_(u"Monday"), _(u"Tuesday"), _(u"Wednesday"), _(u"Thursday"), _(u"Friday"), _(u"Saturday"), _(u"Sunday")]
    if value is None:
        return days
    return days[value]