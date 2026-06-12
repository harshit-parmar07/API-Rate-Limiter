# Distributed API Rate Limiter (Node.js & Redis)

A robust, production-grade distributed traffic management layer designed to provide global, thread-safe endpoint protection across multi-node server clusters. 

This branch switches from single-node in-memory tracking to an asynchronous, externalized key-value storage layer to prevent rate-limiter state loss during scaling, deployments, or server restarts. 🖥️❌

---

## 🏛️ Project Architectures

This repository is organized across branches to demonstrate different architectural trade-offs:

1. **In-Memory Rate Limiter (Core Java) — [Main Branch]**
   * Optimized for single-node deployments requiring ultra-low latency without external network dependencies.
   * Switch to the Java version: [`main`](../../tree/main)
2. **Distributed Rate Limiter (Node.js & Redis) — [Current Branch]**
   * Engineered for cloud-native, multi-node cluster environments requiring globally synchronized state and atomic scaling.

---

## ⚡ Key Features (Distributed Version)

* **Distributed State Management:** Centralized client tracking using Redis, ensuring consistent global rate limiting even when API traffic is split across multiple load-balanced web servers. 🌐📈
* **Token Bucket Algorithm:** Implements a highly responsive token verification system to validate incoming payloads and throttle excess traffic smoothly. 🪣 Throttling calculations are executed dynamically via timestamps.
* **Atomic Transaction Safety:** Utilizes structured Redis transactions to perform atomic operations, completely eliminating multi-server race conditions under high concurrency. 🛡️🏎️
* **Fail-Open Architecture:** Built a resilient Express.js middleware integration featuring native error boundaries to ensure complete API availability to end-users during cache or database downtime. 🛠️🔓
* **Standardized Metadata Injection:** Automatically tracks and appends vital HTTP context headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`, and `Retry-After`) to incoming client responses. 🏷️📊

---

## 📂 Project Structure

```text
examples/
  server.js             // Sample Express application using the middleware
src/
  scripts/
    tokenBucket.lua     // Atomic operation transactions
  strategies/
    BaseStrategy.js     // Abstract base class
    TokenBucket.js      // Redis implementation of token bucket
  RateLimiter.js        // Gateway class & configuration router
  RedisClient.js        // Singleton Redis connection manager
```

---

## ⚙️ Tech Stack

* **Runtime:** Node.js (v18+)
* **Framework:** Express.js 🚀
* **Storage Layer:** Redis (via `ioredis` client) 🗄️
* **Configuration:** Environment isolation via `dotenv`

---

## 🛠️ Local Setup & Execution

Follow these steps to configure, install, and run the distributed rate limiter locally on your machine.

### Prerequisites

* Ensure you have **Node.js** (v18 or higher) installed:
```bash
node -v
```
* Ensure you have a running **Redis instance** (either running locally via `redis-server` or an online cloud provider like Upstash / Redis Cloud).

### 1. Switch to the Distributed Branch
If you haven't already, fetch and checkout the correct branch:
```bash
git checkout distributed-rate-limiter
```

### 2. Environment Configuration
Create a `.env` file in the root directory of the project and add your Redis connection string:
```text
REDIS_URL=redis://127.0.0.1:6379
PORT=3000
```

### 3. Install Dependencies
Install the required node modules (`express`, `ioredis`, and `dotenv`):
```bash
npm install
```

### 4. Run the Application
Start the example Express server:
```bash
node examples/server.js
```

### 5. Verify Rate Limiting Locally
Open a separate terminal window and simulate rapid traffic using `curl` to test the Token Bucket threshold (configured for 5 requests per minute):
```bash
curl -i http://localhost:3000/
```
Repeatedly running this command 6 times will trigger a `429 Too Many Requests` response, showcasing the distributed middleware actively blocking excess payloads. 🚦🛑
