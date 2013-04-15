from django.shortcuts import (render_to_response,
                             RequestContext)
from django.http import Http404
from django.contrib import messages

from messagewall.forms import MessageForm
from messagewall.models import RMessage


def index(request):

    # Get messages information
    count = RMessage.count()

    context = {
        "number_of_messages": count
    }

    return render_to_response('messagewall/index.html',
                              context,
                              context_instance=RequestContext(request))


def show_messages(request):

    if request.method == "POST":
        form = MessageForm(data=request.POST)
        if form.is_valid():
            data = form.cleaned_data

            message = RMessage(data['message'], data['by'], data['to'])
            message.save()
            messages.success(request, "Your new message is posted")

            # Create again the form
            form = MessageForm()
    else:
        form = MessageForm()

    # Get messages information
    rmessages = RMessage.get_all()
    print(rmessages)

    context = {
        "form": form,
        "rmessages": rmessages
    }

    return render_to_response('messagewall/messages.html',
                              context,
                              context_instance=RequestContext(request))
