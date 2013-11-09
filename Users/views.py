from django.shortcuts import render_to_response, get_object_or_404
from Users.models import JadeBusemUser
from forms import RegisterForm
from django.template import Context
from forms import SignInForm
from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.contrib.auth.hashers import check_password
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
                    return render_to_response('user/thanks.html')
                except:
                    email_error = "This e-mail is already in use"
            else:
                password_error = "Passwords do not match"
        else:
            password_error = "Password must be at least six characters long"

    # if error than display form again but with filled fields and error
    if (email_error != "" or password_error != ""):
        f = RegisterForm()
        context = Context(
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
    return render_to_response('user/registration.html', context)


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
                return HttpResponseRedirect('/users/user_panel/'+str(user.user_id))
            else:
                error = True
    else:
        form = SignInForm()
    return render(request, 'user/sign_in.html', {'form': form, 'error': error})

def user_panel(request, userid):
    u = get_object_or_404(JadeBusemUser, user_id=userid)
    email_error = ""
    password_error = ""
    confirm_error = ""
    success = ""
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
                        success = "Changes have been saved!"
                    except:
                        email_error = "This e-mail is already in use"
                else:
                    password_error = "Passwords do not match"
            else:
                password_error = "Password must be at least six characters long"
        else:
            confirm_error = "Password is incorrect"
            
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
                            'confirm_error': confirm_error
                            })
    else:
        context = Context({
                           'email': u.email,
                           'first_name': u.first_name,
                           'last_name': u.last_name,
                            'address': u.address,
                            'company_name': u.company_name,
                            'success': success,
                            })
    return render_to_response('user/user_panel.html', context)