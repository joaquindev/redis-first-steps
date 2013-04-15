from django import forms
from django.utils.translation import ugettext_lazy as _


class MessageForm(forms.Form):
    message = forms.CharField(
        label=_(u'message'),
        widget=forms.Textarea(attrs={
            'style': "width:100%",
            'rows': 4,
            'placeholder': _(u'message')})
    )

    by = forms.CharField(
        label=_(u'by'),
        widget=forms.TextInput(attrs={'placeholder': _(u'by')})
    )
    to = forms.CharField(
        label=_(u'to'),
        widget=forms.TextInput(attrs={'placeholder': _(u'to')})
    )
