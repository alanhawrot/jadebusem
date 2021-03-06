from django.contrib.admin import ModelAdmin
from django.contrib import admin
from models import Schedule,ScheduleDate,ScheduleTracePoint
from django.contrib.admin import widgets
from django.utils.translation import ugettext as _
from django.contrib.admin.widgets import AdminTextInputWidget
from Users.models import JadeBusemUser
from django.forms import ModelForm, forms
widgets.AdminFileWidget
class TracePointAdmin(admin.TabularInline):
    model = ScheduleTracePoint
    extra = 0

class ScheduleDateAdmin(admin.TabularInline):
    model = ScheduleDate
    extra = 0

class ScheduleAdmin(ModelAdmin):
    fields = (_('author'), _('company'), _('image_path'), _('verified'))
    list_display = (_('author'), _('company'), _('image_path'), _('verified'))
    inlines = [TracePointAdmin, ScheduleDateAdmin,]

admin.site.register(Schedule, ScheduleAdmin)
