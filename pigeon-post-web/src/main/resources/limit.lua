--KEYS[1]: 限流 key
--ARGV[1]: 限流窗口,毫秒
--ARGV[2]: 当前时间戳（作为score）
--ARGV[3]: 阈值
--ARGV[4]: score 对应的唯一value
-- 1\. 移除开始时间窗口之前的数据
redis.call('zremrangeByScore', KEYS[1], 0, ARGV[2]-ARGV[1])
-- 2\. 统计当前未过期元素数量
local res = redis.call('zcard', KEYS[1])
-- 3\. 是否超过阈值
if (res == nil) or (res < tonumber(ARGV[3])) then
    redis.call('zadd', KEYS[1], ARGV[2], ARGV[4]) --ARGV[4]为成员标识，ARGV[2]为score
    redis.call('expire', KEYS[1], ARGV[1]/1000) --为整个键设置过期时间，实际是合理的，因为这样一旦过期，证明里面的最后加入的元素过期，即所有元素都过期
    return 0
else
    return 1
end
