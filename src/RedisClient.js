const Redis = require('ioredis');

class RedisClient {
    constructor() {
        if (!RedisClient.instance) {
            RedisClient.instance = this;
            this.client = null;
        }
        return RedisClient.instance;
    }

    connect(url) {
        if (!this.client) {
            this.client = new Redis(url);

            this.client.on('error', (err) => {
                console.error('Redis Client Error:', err);
            });
        }
        return this.client;
    }

    getClient() {
        if (!this.client) {
            throw new Error('Redis client not initialized. Call connect() first.');
        }
        return this.client;
    }
}

module.exports = new RedisClient();
