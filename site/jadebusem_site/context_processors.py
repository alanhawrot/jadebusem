from django.core.exceptions import ObjectDoesNotExist
from Users.models import JadeBusemUser
from topics.models import Contributor, LastTopicCheck

__author__ = 'michal'

from django.utils import translation


def language_processor(request):
    return {'current_lang': translation.get_language()}


def new_topics_processor(request):
    if 'email' in request.session:
        user = JadeBusemUser.objects.get(user_id=request.session['user_id'])
        for contributor in Contributor.objects.filter(contributor=user):
            try:
                last_topic_check = LastTopicCheck.objects.get(topic=contributor.topic, user=user)
                last_check = last_topic_check.last_check
            except ObjectDoesNotExist:
                return {'new_messages': True}
            if contributor.topic.messages.filter(date__gt=last_check):
                return {'new_messages': True}
    return {}