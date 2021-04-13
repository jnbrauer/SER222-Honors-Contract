package com.jnbrauer.data;

public class Interval {
    private final int start;
    private final int end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int overlap(Interval other) {
        return Math.max(0, Math.min(this.end, other.end) - Math.max(this.start, other.start));
    }
}
