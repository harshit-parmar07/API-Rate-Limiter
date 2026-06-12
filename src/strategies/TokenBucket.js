const fs = require('fs');
const path = require('path');
const BaseStrategy = require('./BaseStrategy');

const LUA_SCRIPT = fs.readFileSync(path.join(__dirname, '../scripts/tokenBucket.lua'), 'utf8');

class TokenBucket extends BaseStrategy {
    constructor(redisClient, keyPrefix) {
        super(redisClient, keyPrefix);

        // Define the custom command
        if (!this.redis.status || this.redis.status === 'ready') {
            this.defineCommand();
        } else {
            this.redis.on('ready', () => this.defineCommand());
        }
    }

    defineCommand() {
        // Check if command already exists to avoid errors on potential re-instantiation
        if (!this.redis.tokenBucket) {
            this.redis.defineCommand('tokenBucket', {
                numberOfKeys: 1,
                lua: LUA_SCRIPT,
            });
        }
    }

    /**
     * @param {string} identifier
     * @param {object} options { limit: number, window: number, cost: number }
     * limit: max tokens (capacity)
     * window: refill period (seconds) - implied rate = limit / window ? No, effectively rate
     * Actually, let's interpret options as:
     * capacity: max burst
     * refillRate: tokens added per second
     * OR standard: limit requests per window seconds.
     * Let's stick to standard: limit (capacity) requests per window (seconds).
     * Refill rate = limit / window.
     */
    async hit(identifier, options) {
        const { limit, window } = options;
        const cost = options.cost || 1;

        const key = `${this.keyPrefix}tb:${identifier}`;
        const refillRate = limit / window;
        const now = Date.now() / 1000; // seconds

        // Call lua script
        // ARGV[1]: capacity
        // ARGV[2]: refill rate
        // ARGV[3]: current timestamp
        // ARGV[4]: requested tokens (cost)

        // Note: defineCommand adds the method to the redis instance.
        // Ensure it's defined.
        if (!this.redis.tokenBucket) {
            this.defineCommand();
        }

        const result = await this.redis.tokenBucket(
            key,
            limit,
            refillRate,
            now,
            cost
        );

        const allowed = result[0] === 1;
        const remaining = result[1]; // Current tokens

        // Reset time is roughly when bucket is full? Or just 'window' seconds from now if empty?
        // For Token Bucket, 'reset' isn't as clear cut as "window resets at X".
        // We can estimate time to full: (capacity - current) / rate
        const timeToFull = (limit - remaining) / refillRate;
        const resetTime = Math.ceil(now + timeToFull);

        return {
            allowed,
            remaining: Math.floor(remaining),
            resetTime,
        };
    }
}

module.exports = TokenBucket;
