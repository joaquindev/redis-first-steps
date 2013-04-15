import json

from django.shortcuts import (render_to_response,
                              RequestContext)
from django.contrib import messages
from django.http import HttpResponse, Http404
from django.template.defaultfilters import date as _date

from messagewall.forms import MessageForm
from messagewall.models import RMessage
from messagewall.utils import unix_timestamp_to_datetime


MESSAGES_PER_PAGE = 5


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
    rmessages = RMessage.get_all(0, MESSAGES_PER_PAGE)
    print(rmessages)

    context = {
        "form": form,
        "rmessages": rmessages
    }

    return render_to_response('messagewall/messages.html',
                              context,
                              context_instance=RequestContext(request))


def ajax_show_messages(request):
    page = int(request.GET.get('page', 0))
    offset = page * MESSAGES_PER_PAGE
    count = MESSAGES_PER_PAGE

    rmessages = RMessage.get_all(offset, count)

    if len(rmessages) == 0:
        raise Http404

    return_data = []
    for i in rmessages:
        m = i.__dict__
        m['date'] = _date(unix_timestamp_to_datetime(
            m['date']),
            "M j, Y \a\t H:i:s"
        )
        return_data.append(m)

    return HttpResponse(json.dumps(return_data),
                        mimetype="application/json")
