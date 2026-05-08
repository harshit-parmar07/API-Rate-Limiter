package com.ratelimiter;

public class RateLimitOptions {
    private final int limit;
    private final int window; // in seconds
    private final int cost;

    public RateLimitOptions(int limit, int window) {
        this(limit, window, 1);
    }

    public RateLimitOptions(int limit, int window, int cost) {
        this.limit = limit;
        this.window = window;
        this.cost = cost;
    }

    public int getLimit() {
        return limit;
    }

    public int getWindow() {
        return window;
    }

    public int getCost() {
        return cost;
    }
}
