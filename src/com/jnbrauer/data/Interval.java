package com.jnbrauer.data;

/**
 * An interval of time.
 *
 * @author Jude Brauer
 */
public class Interval implements Comparable<Interval> {
    private final String description;
    private final int start;
    private final int end;

    /**
     * Intitialize an interval with the given title, start time, and end time
     * @param description title
     * @param start start time in minutes
     * @param end end time in minutes
     */
    public Interval(String description, int start, int end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start time of this interval.
     * @return start time
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end time of this interval.
     * @return end time
     */
    public int getEnd() {
        return end;
    }

    /**
     * Get the overlap in minutes of this interval with another interval.
     * @param other interval to check overlap with
     * @return amount of overlap in minutes
     */
    public int overlap(Interval other) {
        return Math.max(0, Math.min(this.end, other.end) - Math.max(this.start, other.start));
    }

    /**
     * Compare this interval to another interval by start time.
     * @param other interval to compare to
     * @return 0 if the start times are equal, -1 if this start time is less than other's time, 1 if this start time is
     * greater than other's start time
     */
    @Override
    public int compareTo(Interval other) {
        return Integer.compare(this.start, other.start);
    }

    /**
     * Get the description of this interval.
     * @return interval description
     */
    public String getDescription() {
        return description;
    }
}
