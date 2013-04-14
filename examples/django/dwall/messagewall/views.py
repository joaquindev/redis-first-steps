from django.shortcuts import (render_to_response,
                             RequestContext)
from django.http import Http404
from django.contrib import messages


def index(request):


    # Get messages information

    data = {
    }

    return render_to_response('messagewall/index.html',
                              data,
                              context_instance=RequestContext(request))


def show_messages(request):


    # Get messages information

    data = {
    }

    return render_to_response('messagewall/index.html',
                              data,
                              context_instance=RequestContext(request))
