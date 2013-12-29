from django.shortcuts import render


def index(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/index.html', {'login': True, 'user': request.session})
    else:
        return render(request, 'jadebusem_site/index.html')


def about(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/about.html', {'login': True, 'user': request.session})
    else:
        return render(request, 'jadebusem_site/about.html')


def language(request):
    if 'email' in request.session:
        return render(request, 'jadebusem_site/language.html', {'login': True, 'user': request.session})
    else:
        return render(request, 'jadebusem_site/language.html')