# Java API Rate Limiter

A lightweight, thread-safe, in-memory API Rate Limiter built entirely in Core Java with zero external dependencies.

This project demonstrates the translation of backend system design concepts (specifically the **Token Bucket algorithm**) into a robust Core Java utility, focusing on Object-Oriented Design (OOP) and high-performance concurrency.

---

## 🏛️ Project Architectures

This repository is organized across branches to demonstrate different architectural trade-offs:

1. **In-Memory Rate Limiter (Core Java) — [Current Branch]**
   * Optimized for single-node deployments requiring ultra-low latency without external network dependencies.
2. **Distributed Rate Limiter (Node.js & Redis) — [Feature Branch]**
   * Engineered for cloud-native, multi-node cluster environments requiring globally synchronized state and atomic scaling.
   * Switch to the distributed version: [`distributed-rate-limiter`](../../tree/distributed-rate-limiter)

---

## ✨ Key Features

- **Token Bucket Algorithm:** Implements standard rate-limiting logic using lazy evaluation to calculate token replenishment on-the-fly, avoiding the overhead of continuous background threads.
- **Thread-Safe State Management:** Utilizes `ConcurrentHashMap.compute()` to guarantee atomic, lock-free updates to user buckets in a multithreaded environment.
- **Strategy Design Pattern:** Built with a modular, interface-driven architecture (`RateLimitStrategy`) making it easy to extend with other algorithms (like Sliding Window or Leaky Bucket) in the future.
- **Fail-Open Architecture:** Wraps core execution in safe try-catch blocks to ensure backend APIs remain accessible even if the rate limiter encounters an internal error.
- **Zero Dependencies:** Pure, vanilla Java. No Spring, no Redis, no external libraries.

---

## 🧠 Why This Project?

While enterprise rate limiters often rely on distributed caches like Redis, I built this strictly in-memory version from scratch to deeply understand the underlying algorithms and Java's concurrency models.

---

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

---

## 🛠️ Local Setup & Execution

Follow these steps to configure and run the project locally on your machine.

### Prerequisites
Ensure you have the Java Development Kit (JDK 17 or higher) installed on your system. You can verify your installation by running:
```bash
java -version
```

### 1. Clone the Repository
Clone the project and navigate into the root directory:
```bash
git clone [https://github.com/YOUR_GITHUB_USERNAME/YOUR_REPOSITORY_NAME.git](https://github.com/YOUR_GITHUB_USERNAME/YOUR_REPOSITORY_NAME.git)
cd YOUR_REPOSITORY_NAME
```

### 2. Compile the Source Code
Compile all Java files into a target binary directory (`bin`):
```bash
javac -d bin src/main/java/com/ratelimiter/**/*.java
```

### 3. Run the Multithreaded Test Harness
Execute the compiled application. The `Main` class simulates a highly concurrent environment by launching multiple parallel threads making rapid requests to test the thread safety of the Token Bucket algorithm:
```bash
java -cp bin com.ratelimiter.Main
```
