# Java API Rate Limiter

A lightweight, thread-safe, in-memory API Rate Limiter built entirely in Core Java with zero external dependencies.

This project demonstrates the translation of backend system design concepts (specifically the **Token Bucket algorithm**) into a robust Core Java utility, focusing on Object-Oriented Design (OOP) and high-performance concurrency.

## ✨ Key Features

- **Token Bucket Algorithm:** Implements standard rate-limiting logic using lazy evaluation to calculate token replenishment on-the-fly, avoiding the overhead of continuous background threads.
- **Thread-Safe State Management:** Utilizes `ConcurrentHashMap.compute()` to guarantee atomic, lock-free updates to user buckets in a multithreaded environment.
- **Strategy Design Pattern:** Built with a modular, interface-driven architecture (`RateLimitStrategy`) making it easy to extend with other algorithms (like Sliding Window or Leaky Bucket) in the future.
- **Fail-Open Architecture:** Wraps core execution in safe try-catch blocks to ensure backend APIs remain accessible even if the rate limiter encounters an internal error.
- **Zero Dependencies:** Pure, vanilla Java. No Spring, no Redis, no external libraries.

## 🧠 Why This Project?

While enterprise rate limiters often rely on distributed caches like Redis, I built this strictly in-memory version from scratch to deeply understand the underlying algorithms and Java's concurrency models.

## 📂 Project Structure

```text
src/main/java/com/ratelimiter/
├── RateLimiter.java              // Context class & main entry point
├── RateLimitOptions.java         // Configuration POJO (limit, window)
├── RateLimitResponse.java        // Response POJO (allowed, remaining, resetTime)
├── Main.java                     // Multithreaded test harness
└── strategy/
    ├── RateLimitStrategy.java    // Interface contract
    └── TokenBucketStrategy.java  // Core algorithm implementation
```
