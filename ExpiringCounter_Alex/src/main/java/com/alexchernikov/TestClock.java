package com.alexchernikov;

public class TestClock implements Clock {

    private long now;

    public TestClock() {
        this(0L);
    }

    public TestClock(long startTime) {
        if(startTime <0) {
            throw new IllegalArgumentException("startTime must be non-negative");
        }
        this.now = startTime;
    }

    @Override
    public synchronized long now() {
        return now;
    }

    public synchronized void advance(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Cannot advance time backwards");
        }
        now += millis;
    }

    public synchronized void tick() {
        now++;
    }
}