require('dotenv').config();
const express = require('express');
const RateLimiter = require('../src/RateLimiter');

const app = express();
const port = process.env.PORT || 3000;

// Initialize RateLimiter
const rateLimiter = new RateLimiter({
    redisUrl: process.env.REDIS_URL || 'redis://localhost:6379',
    algorithm: 'TOKEN_BUCKET',
    keyPrefix: 'rl:example:',
});

// Middleware
const rateLimitMiddleware = async (req, res, next) => {
    const ip = req.ip || req.connection.remoteAddress;

    // Limit: 5 requests per 60 seconds
    const limits = { limit: 5, window: 60 };

    try {
        const result = await rateLimiter.hit(ip, limits);

        // Set Headers
        res.set('X-RateLimit-Limit', limits.limit);
        res.set('X-RateLimit-Remaining', result.remaining);

        if (result.resetTime) {
            // resetTime is usually in seconds (unix epoch)
            res.set('X-RateLimit-Reset', result.resetTime);
        }

        if (result.allowed) {
            next();
        } else {
            const retryAfter = Math.ceil(result.resetTime - (Date.now() / 1000));
            res.set('Retry-After', retryAfter);
            res.status(429).send('Too Many Requests');
        }
    } catch (error) {
        console.error('Middleware Error:', error);
        // Fail Open
        next();
    }
};

app.use(rateLimitMiddleware);

app.get('/', (req, res) => {
    res.send('Hello! Request allowed.');
});

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`);
    console.log(`Rate Limiting: 5 connects per minute.`);
});
