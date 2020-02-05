from django.shortcuts import render
from django.shortcuts import HttpResponse
from login import models

# Create your views here.

user_list = []


def index(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        # save data to database
        models.UserInfo.objects.create(user=username, pwd=password)

    # read data from database
    user_list = models.UserInfo.objects.all()
    return render(request, 'index.html', {'data': user_list})
