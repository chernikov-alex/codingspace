package com.alexchernikov;

import java.util.Deque;
import java.util.LinkedList;

public class ExpiringCounter {

    private final long windowMillis;
    private final Clock clock;

    private final Deque<Bucket> buckets = new LinkedList<>();
    private long totalCount = 0;

    public ExpiringCounter(long windowMillis, Clock clock) {
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    public void record() {
        long now = clock.now();
        long bucketLowerBound = (now / 1000) * 1000;

        synchronized (this) {
            expireBuckets(now);

            Bucket bucket = buckets.peekLast();
            if (bucket == null || bucket.lowerBound != bucketLowerBound) {
                bucket = new Bucket(bucketLowerBound);
                buckets.addLast(bucket);
            }

            int index = (int) (now % 1000);
            bucket.increment(index);
            totalCount++;
        }
    }

    public long getCount() {
        long now = clock.now();

        synchronized (this) {
            expireBuckets(now);
            return totalCount;
        }
    }

    /**
     * Remove expired buckets based on inclusive boundary rule:
     * (now - timestamp) <= windowMillis  → valid
     * (now - timestamp) >  windowMillis  → expired
     */
    private void expireBuckets(long now) {
        long windowStart = now - windowMillis;

        while (!buckets.isEmpty()) {
            Bucket oldest = buckets.peekFirst();
            long bucketStart = oldest.lowerBound;
            long bucketEndTime = bucketStart + 999;

            if (now - bucketEndTime > windowMillis) {
                totalCount -= oldest.total;
                buckets.removeFirst();
            } else {
                // Partial expiration
                if (bucketStart < windowStart) {
                    int expireUntil = (int) (windowStart - bucketStart);
                    expireInsideBucket(oldest, expireUntil);
                }
                break;
            }
        }
    }

    private void expireInsideBucket(Bucket bucket, int expireUntil) {
        for (int i = 0; i < expireUntil; i++) {
            totalCount -= bucket.millis[i];
            bucket.total -= bucket.millis[i];
            bucket.millis[i] = 0;
        }
    }

    private static final class Bucket {
        final long lowerBound;
        final int[] millis = new int[1000];
        long total = 0;

        Bucket(long lowerBound) {
            this.lowerBound = lowerBound;
        }

        void increment(int index) {
            millis[index]++;
            total++;
        }
    }
}
