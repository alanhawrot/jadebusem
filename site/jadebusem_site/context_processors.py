__author__ = 'michal'

from django.utils import translation

def language_processor(request):
    return {'current_lang': translation.get_language()}