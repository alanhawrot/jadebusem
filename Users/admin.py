from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from django.utils.translation import ugettext_lazy as _

from Users.models import JadeBusemUser
from Users.forms import JadeBusemUserChangeForm, JadeBusemUserCreationForm

class JadeBusemUserAdmin(UserAdmin):
    # The forms to add and change user instances

    # The fields to be used in displaying the User model.
    # These override the definitions on the base UserAdmin
    # that reference the removed 'username' field
    

    
    fieldsets = (
        (None, {'fields': ('email', 'password')}),
        (_('Personal info'), {'fields': ('first_name', 'last_name', 'address', 'company_name')}),
        (_('Permissions'), {'fields': ('is_active', 'is_staff', 'is_superuser',
                                       'groups', 'user_permissions')}),
        (_('Important dates'), {'fields': ('last_login', 'date_joined')}),
    )
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'password1', 'password2')}
        ),
    )
    form = JadeBusemUserChangeForm
    add_form = JadeBusemUserCreationForm
    list_display = ('email', 'first_name', 'last_name', 'address', 'company_name', 'is_staff')
    search_fields = ('email', 'first_name', 'last_name', 'address', 'company_name')
    ordering = ('email',)

admin.site.register(JadeBusemUser, JadeBusemUserAdmin)