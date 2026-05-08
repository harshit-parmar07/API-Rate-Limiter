package com.ratelimiter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Core Java Rate Limiter Example...");

        RateLimiter rateLimiter = new RateLimiter(RateLimiter.Algorithm.TOKEN_BUCKET);
        // Configuration: 5 requests allowed per 1-second window.
        RateLimitOptions options = new RateLimitOptions(5, 1);

        String identifier = "user_123";

        // Simulate 10 concurrent requests at exactly the same time
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger allowedCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    RateLimitResponse response = rateLimiter.hit(identifier, options);
                    
                    if (response.isAllowed()) {
                        allowedCount.incrementAndGet();
                        System.out.println(String.format("Thread %4d - ALLOWED.  Remaining: %d", 
                            Thread.currentThread().threadId(), response.getRemaining()));
                    } else {
                        rejectedCount.incrementAndGet();
                        System.out.println(String.format("Thread %4d - REJECTED. Remaining: %d", 
                            Thread.currentThread().threadId(), response.getRemaining()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        System.out.println("Releasing all " + numThreads + " concurrent threads...");
        startLatch.countDown();
        endLatch.await(); // Wait for all threads to finish
        executor.shutdown();

        System.out.println("\n--- Concurrency Results ---");
        System.out.println("Total Allowed:  " + allowedCount.get() + " (Expected: 5)");
        System.out.println("Total Rejected: " + rejectedCount.get() + " (Expected: 5)");

        // Wait 1.1 seconds for tokens to refill completely
        System.out.println("\nWaiting for 1.1 seconds for tokens to refill...");
        Thread.sleep(1100);

        RateLimitResponse finalResponse = rateLimiter.hit(identifier, options);
        System.out.println("After wait - Allowed: " + finalResponse.isAllowed() + 
            ", Remaining: " + finalResponse.getRemaining()); // Expected Allowed = true, Remaining = 4
    }
}
