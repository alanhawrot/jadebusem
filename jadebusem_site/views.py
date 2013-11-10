from django.shortcuts import render


def index(request):
    return render(request, 'jadebusem_site/index.html')


def about(request):
    return render(request, 'jadebusem_site/about.html')