-- tokenBucket.lua
-- KEYS[1]: key (e.g., rl:ip:127.0.0.1)
-- ARGV[1]: capacity (max tokens)
-- ARGV[2]: refill rate (tokens per second)
-- ARGV[3]: current timestamp (seconds)
-- ARGV[4]: requested tokens

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- Get current tokens and last refill time
local info = redis.call('HMGET', key, 'tokens', 'last_refill')
local current_tokens = tonumber(info[1])
local last_refill = tonumber(info[2])

if current_tokens == nil then
  current_tokens = capacity
  last_refill = now
end

-- Refill tokens
local elapsed = now - last_refill
if elapsed > 0 then
  local new_tokens = elapsed * refill_rate
  current_tokens = math.min(capacity, current_tokens + new_tokens)
  last_refill = now
end

-- Check if enough tokens
local allowed = 0
if current_tokens >= requested then
  current_tokens = current_tokens - requested
  allowed = 1
end

-- Update Redis
redis.call('HMSET', key, 'tokens', current_tokens, 'last_refill', last_refill)
-- Set expiry to avoid stale keys (e.g., 2 * capacity / refill_rate or a safe default)
redis.call('EXPIRE', key, math.ceil(capacity / refill_rate * 2)) -- Prevent from one time visitors

-- Return: allowed, remaining tokens, reset time (not strictly applicable, but returning 0 for consistency or calculated time to full)
return {allowed, current_tokens}
