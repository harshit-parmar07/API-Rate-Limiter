class BaseStrategy {
    constructor(redisClient, keyPrefix) {
        if (this.constructor === BaseStrategy) {
            throw new Error("Abstract classes can't be instantiated.");
        }
        this.redis = redisClient;
        this.keyPrefix = keyPrefix || 'rl:';
    }

    /**
     * @param {string} identifier
     * @param {object} options
     * @returns {Promise<{allowed: boolean, remaining: number, resetTime: number}>}
     */
    async hit(identifier, options) {
        throw new Error("Method 'hit()' must be implemented.");
    }
}

module.exports = BaseStrategy;
