package com.ratelimiter;

public class RateLimitResponse {
    private final boolean allowed;
    private final int remaining;
    private final long resetTime; // Unix timestamp in seconds

    public RateLimitResponse(boolean allowed, int remaining, long resetTime) {
        this.allowed = allowed;
        this.remaining = remaining;
        this.resetTime = resetTime;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemaining() {
        return remaining;
    }

    public long getResetTime() {
        return resetTime;
    }

    @Override
    public String toString() {
        return "RateLimitResponse{" +
                "allowed=" + allowed +
                ", remaining=" + remaining +
                ", resetTime=" + resetTime +
                '}';
    }
}
