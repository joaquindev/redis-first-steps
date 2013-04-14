import uuid

import utils


class RMessage(object):

    MESSAGE_KEY = "Mdiss:message:{0}"
    MESSAGES_KEY = "Mdiss:messages"

    # We are going to use lua to retrieve the data. we desing the data so in a
    # way that we need to save/retrieve in two steps (look at the list adn then
    # look at the message data)

    LUA_SAVE = """

        local id = ARGV[1]
        local msg = ARGV[2]
        local by = ARGV[3]
        local to = ARGV[4]
        local date = tonumber(ARGV[5])
        local res

        -- Save the id in the list
        redis.call('lpush',KEYS[1], id)

        -- Save the data of the message
        res = redis.call('hmset',KEYS[2], "id", id, "message", msg, "by", by, "to", to, "date", date)
        return res
    """

    LUA_RETRIEVE = """
        local messages = {}
        local min = tonumber(ARGV[1])
        local max = tonumber(ARGV[2])

        -- Get all the IDs
        local messageIds = redis.call('lrange',KEYS[1], min, max)

        --And now with the IDs get all the messages
        for key, value in pairs(messageIds) do
            local temp_message = redis.call('hgetall', "Mdiss:message:"..value)
            table.insert(messages, temp_message)
        end
        return  messages
    """

    def __init__(self, message, by, to):
        self.id = str(uuid.uuid4())
        self.message = message
        self.by = by
        self.to = to
        self.date = utils.get_unix_timestamp_now()
        super(RMessage, self).__init__()

    def __str__(self):
        str_format = "{0} - {1} by {2} to {3} on {4}"
        return str_format.format(self.id,
                                 self.message,
                                 self.by,
                                 self.to,
                                 self.date)

    def save(self):
        r = utils.get_redis_connection()
        save_msg = r.register_script(RMessage.LUA_SAVE)

        result = save_msg(
            keys=[
                RMessage.MESSAGES_KEY,
                RMessage.MESSAGE_KEY.format(self.id)
            ],
            args=[
                self.id,
                self.message,
                self.by,
                self.to,
                self.date
            ]
        )
        return result

    @classmethod
    def count(cls):
        r = utils.get_redis_connection()
        return r.llen(RMessage.MESSAGES_KEY)

    @classmethod
    def get_all(cls, offset=0, count=-1):

        messages = []

        r = utils.get_redis_connection()
        get_msgs = r.register_script(RMessage.LUA_RETRIEVE)

        result = get_msgs(
            keys=[
                RMessage.MESSAGES_KEY,
            ],
            args=[
                offset,
                offset + count - 1  # We need the upper limit
            ]
        )

        # Result Style:
        #   [
        #      ['id', '25d8098c-524b-44a8-94ac-4e7255cb6958', 'message', 'message1', 'by', 'sender1', 'to', 'receiver1', 'date', '1365968231'],
        #      ['id', '2107de9c-aa9f-44fc-a8ee-6bf9f57cba74', 'message', 'message0', 'by', 'sender0', 'to', 'receiver0', 'date', '1365968231']
        #   ]

        for i in result:
            msg = RMessage(i[3], i[5], i[7])
            msg.id = i[1]
            msg.date = int(i[9])
            messages.append(msg)

        return messages
