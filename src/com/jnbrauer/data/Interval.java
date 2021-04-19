package com.jnbrauer.data;

public class Interval implements Comparable<Interval> {
    private final String description;
    private final int start;
    private final int end;

    public Interval(String description, int start, int end) {
        this.description = description;
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

    @Override
    public int compareTo(Interval other) {
        return Integer.compare(this.start, other.start);
    }

    public String getDescription() {
        return description;
    }
}
