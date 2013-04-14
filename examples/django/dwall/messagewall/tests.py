"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase

from messagewall.models import RMessage
from messagewall import utils


class RedisMessageTest(TestCase):
    def setUp(self):
        self.r = utils.get_redis_connection()

    def tearDown(self):
        self.r.flushdb()

    def test_save(self):

        msg = RMessage("Microsoft is crap", "Richard Stallman", "Bill Gates")
        msg.save()

        r = utils.get_redis_connection()
        res = r.hgetall(RMessage.MESSAGE_KEY.format(msg.id))

        self.assertEquals(1, r.llen(RMessage.MESSAGES_KEY))
        self.assertEquals(msg.date, int(res['date']))
        self.assertEquals(msg.id, res['id'])
        self.assertEquals(msg.by, res['by'])
        self.assertEquals(msg.to, res['to'])
        self.assertEquals(msg.message, res['message'])

    def test_get_count(self):
        number_of_messages = 100

        for i in range(number_of_messages):
            m = RMessage("test", "test", "test")
            m.save()

        self.assertEquals(number_of_messages, RMessage.count())

    def test_get_all(self):
        msg = "message{0}"
        sender = "sender{0}"
        receiver = "receiver{0}"
        number_of_messages = 100

        for i in range(number_of_messages):
            m = RMessage(msg.format(i),
                         sender.format(i),
                         receiver.format(i))
            m.save()

        messages = RMessage.get_all()

        for i in messages:
            number_of_messages = number_of_messages - 1
            self.assertEquals(msg.format(number_of_messages), i.message)
            self.assertEquals(sender.format(number_of_messages), i.by)
            self.assertEquals(receiver.format(number_of_messages), i.to)

    def test_get_all_limit(self):
        msg = "message{0}"
        sender = "sender{0}"
        receiver = "receiver{0}"
        number_of_messages = 100

        # From 90 to 60 (both included)
        offset = 10
        count = 30

        for i in range(number_of_messages):
            m = RMessage(msg.format(i),
                         sender.format(i),
                         receiver.format(i))
            m.save()

        messages = RMessage.get_all(offset, count)

        self.assertEquals(count, len(messages))

        number_of_messages = number_of_messages - offset - 1  # Because we start in 0

        for i in messages:
            self.assertEquals(msg.format(number_of_messages), i.message)
            self.assertEquals(sender.format(number_of_messages), i.by)
            self.assertEquals(receiver.format(number_of_messages), i.to)

            number_of_messages = number_of_messages - 1
