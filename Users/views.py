from django.shortcuts import render_to_response
from Users.models import JadeBusemUser
from forms import RegisterForm
from django.template import Context
  
def register(request):
    email_error = ""
    password_error = ""
    
    if(request.method == 'POST'):
        password = request.POST['password']
        password2 = request.POST['password2'] 
        if(len(password) >= 6):
            if(password == password2):
                try:
                    user = JadeBusemUser.objects.create_user(request.POST['email'],password)
                    user.first_name = request.POST['first_name']
                    user.last_name = request.POST['last_name']
                    user.address = request.POST['address']
                    user.company_name = request.POST['company_name'] 
                    user.save()
                    return render_to_response('user/thanks.html')
                except:
                    email_error = "Podany e-mail jest zajety"
            else:
                password_error = "Podane hasla nie sa identyczne"
        else:
            password_error = "Haslo musi miec co najmniej 6 znakow"
    
    # if error than display form again but with filled fields and error
    if(email_error != "" or password_error != ""): 
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