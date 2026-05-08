package com.ratelimiter.strategy;

import com.ratelimiter.RateLimitOptions;
import com.ratelimiter.RateLimitResponse;

public interface RateLimitStrategy {
    RateLimitResponse hit(String identifier, RateLimitOptions options);
}
