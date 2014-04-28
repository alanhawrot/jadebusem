from django.shortcuts import render_to_response, get_object_or_404
from Users.models import JadeBusemUser
from forms import RegisterForm
from django.template import Context, RequestContext
from forms import SignInForm
from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.contrib.auth.hashers import check_password
from django.utils.translation import ugettext as _
import random


def register(request):
    email_error = ""
    password_error = ""

    if (request.method == 'POST'):
        password = request.POST['password']
        password2 = request.POST['password2']
        if (len(password) >= 6):
            if (password == password2):
                try:
                    user = JadeBusemUser.objects.create_user(request.POST['email'], password)
                    user.user_id = str(random.randint(999999, 9999999))
                    while(1):
                        try:
                            JadeBusemUser.objects.get(user_id=user.user_id)
                            user.user_id = str(random.randint(999999, 999999999))
                        except:
                            break;
                    user.first_name = request.POST['first_name']
                    user.last_name = request.POST['last_name']
                    user.address = request.POST['address']
                    user.company_name = request.POST['company_name']
                    user.save()
                    return render(request, 'user/thanks.html', {})
                except:
                    email_error = _("This e-mail is already in use")
            else:
                password_error = _("Passwords do not match")
        else:
            password_error = _("Password must be at least six characters long")

    # if error than display form again but with filled fields and error
    if (email_error != "" or password_error != ""):
        f = RegisterForm()
        context = RequestContext(
            {'form': f,
             'email': request.POST['email'],
             'first_name': request.POST['first_name'],
             'last_name': request.POST['last_name'],
             'address': request.POST['address'],
             'company_name': request.POST['company_name'],
             'email_error': email_error,
             'password_error': password_error,
            })
    else:
        # Create blank form - we get here only at first time
        f = RegisterForm()
        context = Context({'form': f})
    return render(request, 'user/registration.html', context)


def sign_in(request):
    error = False
    if request.method == 'POST':
        form = SignInForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            email_address = cd['email']
            given_password = cd['password']
            try:
                user = JadeBusemUser.objects.get(email=email_address)
            except JadeBusemUser.DoesNotExist:
                error = True
            if error is False and check_password(given_password, user.password):
                request.session['email'] = user.email
                request.session['name'] = user.first_name
                request.session['user_id'] = user.user_id
                return render(request, 'jadebusem_site/index.html', Context({'user': request.session, 'login': True}))
            else:
                error = True
    else:
        form = SignInForm()
    return render(request, 'user/sign_in.html', Context({'form': form, 'error': error}))

def log_out(request):
    request.session.flush()
    return HttpResponseRedirect('/')

def user_settings(request, userid):
    u = get_object_or_404(JadeBusemUser, user_id=userid)
    email_error = ""
    password_error = ""
    confirm_error = ""
    success = ""
    login = False
    if 'email' in request.session:
        login = True
    if request.method == 'POST':    
        if(u.check_password(request.POST['confirm'])):
            password = request.POST['password']
            password2 = request.POST['password2']
            if(password == "" or len(password) >= 6):
                if(password == password2):
                    try:
                        u.email = request.POST['email']
                        u.first_name = request.POST['first_name']
                        u.last_name = request.POST['last_name']
                        u.address = request.POST['address']
                        u.company_name = request.POST['company_name']
                        u.save()
                        if(len(password) >= 6):
                            u.set_password(password)
                            u.save()
                        success = _("Changes have been saved!")
                        if(u.first_name != ""):
                            request.session['name'] = u.first_name
                        else:
                            request.session['name'] = ""
                    except:
                        email_error = _("This e-mail is already in use")
                else:
                    password_error = _("Passwords do not match")
            else:
                password_error = _("Password must be at least six characters long")
        else:
            confirm_error = _("Password is incorrect")

    if(email_error != "" or password_error != "" or confirm_error != ""): 
        context = Context(
                          {
                           'email': request.POST['email'],
                            'first_name': request.POST['first_name'],
                            'last_name': request.POST['last_name'],
                            'address': request.POST['address'],
                            'company_name': request.POST['company_name'],
                            'email_error': email_error,
                            'password_error': password_error,
                            'confirm_error': confirm_error,
                            'user': request.session,
                            'login': login
                            })
    else:
        context = Context({
                           'email': u.email,
                           'first_name': u.first_name,
                           'last_name': u.last_name,
                            'address': u.address,
                            'company_name': u.company_name,
                            'success': success,
                            'user': request.session,
                            'login': login
                            })
    return render(request, 'user/user_settings.html', context)