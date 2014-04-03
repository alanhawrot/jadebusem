__author__ = 'michal'
from abc import *


class BaseScheduleAccessManager(object):
    __metaclass__ = ABCMeta

    def __init__(self, user=None, schedule=None):
        """

        :type user: JadeBusemUser
        :type schedule: Schedule
        """

        self.__user = user
        self.__schedule = schedule

    @property
    def user(self):
        """

        :rtype: JadeBusemUser
        """

        return self.__user

    @user.setter
    def user(self, user):
        """

        :type user: JadeBusemUser
        """
        self.__user = user

    @property
    def schedule(self):
        """

        :rtype: Schedule
        """
        return self.__schedule

    @schedule.setter
    def schedule(self, schedule):
        """

        :type schedule: Schedule
        """

        self.__schedule = schedule

    @abstractmethod
    def can_read(self):
        pass

    @abstractmethod
    def can_write(self):
        pass

    @abstractmethod
    def can_delete(self):
        pass


class DefaultScheduleAccessManager(BaseScheduleAccessManager):
    def can_delete(self):
        return False

    def can_write(self):
        return self.user.id == self.schedule.author.id

    def can_read(self):
        return True

