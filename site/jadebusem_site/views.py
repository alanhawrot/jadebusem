from django.shortcuts import render
from django.template import Context
from django.views.decorators.cache import cache_page


@cache_page(60 * 15)
def about(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/about.html', {'login': True, 'user': request.session})
    else:
        return render(request, 'jadebusem_site/about.html')


def language(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/language.html', Context({'login': True, 'user': request.session}))
    else:
        return render(request, 'jadebusem_site/language.html', Context({}))