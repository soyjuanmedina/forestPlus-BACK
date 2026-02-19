package com.forestplus.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket newBucket(int capacity, Duration duration) {
        Bandwidth limit = Bandwidth.simple(capacity, duration);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean tryConsumeByIp(String ip) {
        Bucket bucket = cache.computeIfAbsent(
                "IP_" + ip,
                key -> newBucket(5, Duration.ofMinutes(10))
        );
        return bucket.tryConsume(1);
    }

    public boolean tryConsumeByEmail(String email) {
        Bucket bucket = cache.computeIfAbsent(
                "EMAIL_" + email,
                key -> newBucket(3, Duration.ofMinutes(30))
        );
        return bucket.tryConsume(1);
    }
}