from rest_framework import serializers
from rest_framework.pagination import PaginationSerializer
from rest_framework.tests.users.serializers import UserSerializer

from schedules.models import Schedule, ScheduleTracePoint, ScheduleDate


__author__ = 'alanhawrot'


class ScheduleTracePointSerializer(serializers.ModelSerializer):
    class Meta:
        model = ScheduleTracePoint
        fields = ('address', 'position')


class ScheduleDateSerializer(serializers.ModelSerializer):
    class Meta:
        model = ScheduleDate
        fields = ('time', 'day')


class ScheduleSerializer(serializers.ModelSerializer):
    schedule_trace_points = ScheduleTracePointSerializer(many=True)
    schedule_dates = ScheduleDateSerializer(many=True)

    class Meta:
        model = Schedule
        fields = ('trace_points', 'departures')
        depth = 1


class PaginatedScheduleSerializer(PaginationSerializer):
    class Meta:
        object_serializer_class = ScheduleSerializer
