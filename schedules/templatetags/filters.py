# coding=utf-8
__author__ = 'michal'
from django import template

register = template.Library()

@register.filter
def days_of_week(value):
    days = [u"Poniedziałek", u"Wtorek", u"Środa", u"Czwartek", u"Piątek", u"Sobota", u"Niedziela"]
    if value is None:
        return days
    return days[value]