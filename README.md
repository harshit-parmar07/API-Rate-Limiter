# Multi-Architecture API Rate Limiter

A high-performance rate-limiting repository designed to protect RESTful endpoints from excessive request volumes. This project showcases structural engineering patterns across different language runtimes and storage layers to solve rate-limiting challenges at scale.

---

## 🏛️ Project Architectures

This repository is organized across branches to demonstrate different architectural trade-offs:

1. **In-Memory Rate Limiter (Core Java) — [Current Branch]**
   * Optimized for single-node deployments requiring ultra-low latency without external network dependencies.
2. **Distributed Rate Limiter (Node.js & Redis) — [Feature Branch]**
   * Engineered for cloud-native, multi-node cluster environments requiring globally synchronized state and atomic scaling.
   * Switch to the distributed version: [`distributed-rate-limiter`](../../tree/distributed-rate-limiter)

---

## ✨ Key Features (Java In-Memory)

- **Token Bucket Algorithm:** Implements standard rate-limiting logic using lazy evaluation to calculate token replenishment on-the-fly, avoiding the overhead of continuous background threads.
- **Thread-Safe State Management:** Utilizes `ConcurrentHashMap.compute()` to guarantee atomic, lock-free updates to user buckets in a multithreaded environment.
- **Strategy Design Pattern:** Built with a modular, interface-driven architecture (`RateLimitStrategy`) making it easy to extend with other algorithms in the future.
- **Fail-Open Architecture:** Wraps core execution in safe try-catch blocks to ensure backend APIs remain accessible even if the rate limiter encounters an internal error.
- **Zero Dependencies:** Pure, vanilla Java. No Spring, no Redis, no external libraries.

## 🧠 Architectural Trade-Offs

While enterprise rate limiters often rely on distributed caches like Redis, this in-memory version was built from scratch to deeply master the underlying algorithms and Java's memory model under heavy multithreaded concurrency. For a cluster-based, multi-server deployment model, see the distributed branch linked above.

## 📂 Project Structure

```text
src/main/java/com/ratelimiter/
├── RateLimiter.java          
├── RateLimitOptions.java     
├── RateLimitResponse.java    
├── Main.java                 
└── strategy/
    ├── RateLimitStrategy.java
    └── TokenBucketStrategy.java
