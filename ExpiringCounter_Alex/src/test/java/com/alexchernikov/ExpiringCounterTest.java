package com.alexchernikov;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpiringCounterTest {
    @Test
    void testEmptyCounter() {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(1000, clock);

        assertEquals(0, counter.getCount());
    }

    @Test
    void testSimpleRecording() {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(1000, clock);

        counter.record();
        counter.record();

        assertEquals(2, counter.getCount());
    }

    @Test
    void testExpiration() {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(1000, clock);

        clock.advance(0);
        counter.record(); // at T=0

        clock.advance(1000);
        assertEquals(1, counter.getCount()); // inclusive boundary

        clock.advance(1001);
        assertEquals(0, counter.getCount()); // expired
    }

    @Test
    void testRollingWindow() {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(1000, clock);

        counter.record(); // event A at 0

        clock.advance(500);
        counter.record(); // event B at 500

        clock.advance(500); // now = 1000
        assertEquals(2, counter.getCount());
        // A: 1000 - 0 = 1000  -> included
        // B: 1000 - 500 = 500 -> included

        clock.advance(501); // now = 1501
        assertEquals(0, counter.getCount());
        // A: 1501 - 0 = 1501 -> expired
        // B: 1501 - 500 = 1001 -> expired
    }

    @Test
    void testBoundaries() {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(1000, clock);

        clock.advance(10);
        counter.record(); // timestamp = 10

        clock.advance(1010);
        assertEquals(1, counter.getCount()); // 1010 - 10 = 1000 → included

        clock.advance(1011);
        assertEquals(0, counter.getCount()); // expired
    }

    @Test
    void testInvalidInput() {
        TestClock clock = new TestClock();

        assertThrows(IllegalArgumentException.class,
                () -> new ExpiringCounter(-1, clock));
        assertThrows(IllegalArgumentException.class,
                () -> new ExpiringCounter(1000, null));
    }

    @Test
    void testConcurrentRecordingAndCounting() throws Exception {
        TestClock clock = new TestClock();
        ExpiringCounter counter = new ExpiringCounter(10_000, clock);

        int threads = 4;
        int incrementsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.record();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threads * incrementsPerThread, counter.getCount());
    }
}
