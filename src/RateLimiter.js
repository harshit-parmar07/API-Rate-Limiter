const RedisClient = require('./RedisClient');
const TokenBucket = require('./strategies/TokenBucket');

class RateLimiter {
    constructor(config) {
        this.redis = RedisClient.connect(config.redisUrl || 'redis://localhost:6379');
        this.keyPrefix = config.keyPrefix || 'rl:';

        switch (config.algorithm) {
            case 'TOKEN_BUCKET':
                this.strategy = new TokenBucket(this.redis, this.keyPrefix);
                break;
            default:
                throw new Error(`Invalid algorithm: ${config.algorithm}`);
        }
    }

    /**
     * @param {string} identifier
     * @param {object} options
     * @returns {Promise<{allowed: boolean, remaining: number, resetTime: number}>}
     */
    async hit(identifier, options) {
        if (!options || !options.limit || !options.window) {
            throw new Error('Options "limit" and "window" are required.');
        }

        try {
            return await this.strategy.hit(identifier, options);
        } catch (err) {
            console.error('RateLimiter Error:', err);
            // Fail-open
            return {
                allowed: true,
                remaining: options.limit - 1,
                resetTime: Date.now() / 1000 + options.window
            };
        }
    }
}

module.exports = RateLimiter;
