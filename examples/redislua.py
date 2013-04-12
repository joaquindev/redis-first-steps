import redis

fiboLuaScript = """
    local i = tonumber(ARGV[1])
    local first = 0
    local second = 1
    local res

    local function fibo(x, y, max)
        if max ~= 0 then
            res = redis.call('rpush',KEYS[1],x)
            return fibo(y, x+y, max -1)
        else
            return res
        end
    end

    return fibo(first, second, i)
"""


r = redis.Redis()
key = "fibonacci:example"
fibo_digits = 100

r.flushdb()
fibonacci = r.register_script(fiboLuaScript)

result = fibonacci(keys=[key], args=[fibo_digits])
print("result of calling fibonacci with lua in Redis: {0}".format(result))
print("fibonacci result:\n{0}".format(r.lrange(key, 0, -1)))
