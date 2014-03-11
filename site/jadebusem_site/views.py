from django.shortcuts import render
from django.utils import translation

def about(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/about.html', {'login': True, 'user': request.session})
    else:
        return render(request, 'jadebusem_site/about.html')


def language(request):
    current_lang = translation.get_language()
    if 'email' in request.session:
        return render(request, 'jadebusem_site/language.html', {'login': True, 'user': request.session, 'current_lang': current_lang})
    else:
        return render(request, 'jadebusem_site/language.html', {'current_lang': current_lang})