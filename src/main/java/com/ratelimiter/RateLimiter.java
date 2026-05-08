package com.ratelimiter;

import com.ratelimiter.strategy.RateLimitStrategy;
import com.ratelimiter.strategy.TokenBucketStrategy;

public class RateLimiter {

    public enum Algorithm {
        TOKEN_BUCKET
    }

    private final RateLimitStrategy strategy;

    public RateLimiter(Algorithm algorithm) {
        if (algorithm == Algorithm.TOKEN_BUCKET) {
            this.strategy = new TokenBucketStrategy();
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    public RateLimiter(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    public RateLimitResponse hit(String identifier, RateLimitOptions options) {
        if (options == null || options.getLimit() <= 0 || options.getWindow() <= 0) {
            throw new IllegalArgumentException("Options 'limit' and 'window' are required and must be > 0.");
        }

        try {
            return strategy.hit(identifier, options);
        } catch (Exception e) {
            // Log error
            System.err.println("RateLimiter Error: " + e.getMessage());
            e.printStackTrace();

            // Fail-open: Permit the request if an internal exception occurs
            return new RateLimitResponse(
                    true,
                    Math.max(0, options.getLimit() - options.getCost()),
                    (System.currentTimeMillis() / 1000) + options.getWindow());
        }
    }
}
