package com.ratelimiter.strategy;

import com.ratelimiter.RateLimitOptions;
import com.ratelimiter.RateLimitResponse;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimitStrategy {

    private static class BucketState {
        double currentTokens;
        long lastRefillTimestamp; // in milliseconds

        BucketState(double currentTokens, long lastRefillTimestamp) {
            this.currentTokens = currentTokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    // In-memory thread-safe store for buckets
    private final ConcurrentHashMap<String, BucketState> store = new ConcurrentHashMap<>();

    @Override
    public RateLimitResponse hit(String identifier, RateLimitOptions options) {
        final long nowMs = System.currentTimeMillis();
        final int limit = options.getLimit();
        final int window = options.getWindow();
        final int cost = options.getCost();

        // Calculate refill rate (tokens per millisecond)
        final double refillRatePerMs = (double) limit / (window * 1000.0);

        final boolean[] allowedArr = new boolean[1];

        // compute is atomic per key, guaranteeing thread-safety for concurrent requests on the same identifier
        BucketState finalState = store.compute(identifier, (key, currentState) -> {
            if (currentState == null) {
                // Initialize a full bucket, minus the cost (if we can afford it)
                if (limit >= cost) {
                    allowedArr[0] = true;
                    return new BucketState(limit - cost, nowMs);
                } else {
                    allowedArr[0] = false;
                    return new BucketState(limit, nowMs);
                }
            } else {
                long elapsedTimeMs = nowMs - currentState.lastRefillTimestamp;
                double tokensToAdd = elapsedTimeMs * refillRatePerMs;
                double newTokens = Math.min(limit, currentState.currentTokens + tokensToAdd);

                if (newTokens >= cost) {
                    allowedArr[0] = true;
                    newTokens -= cost;
                } else {
                    allowedArr[0] = false;
                }

                return new BucketState(newTokens, nowMs);
            }
        });

        // Calculate reset time: estimated time when the bucket will be completely full.
        double refillRatePerSec = (double) limit / window;
        double timeToFullSec = (limit - finalState.currentTokens) / refillRatePerSec;
        long resetTimeSec = (long) Math.ceil((nowMs / 1000.0) + timeToFullSec);

        return new RateLimitResponse(
                allowedArr[0],
                (int) Math.floor(finalState.currentTokens),
                resetTimeSec
        );
    }
}
