package com.jnbrauer.data;

import java.util.LinkedList;
import java.util.List;

/**
 * A recurring reserved time in which tasks should not be scheduled such as class time or sleeping time.
 *
 * @author Jude Brauer
 */
public class ReservedTime {
    private final String title;

    private final int startOffset;
    private final int duration;
    private final int period;

    /**
     * Create a new reserved time.
     * @param title title of this reserved time
     * @param startOffset the time in minutes from the 0 time at which this reserved time first occurs
     * @param duration the duration of this reserved time
     * @param period the amount of time between the start time of repetitions of this reserved time
     */
    public ReservedTime(String title, int startOffset, int duration, int period) {
        this.title = title;
        this.startOffset = startOffset;
        this.duration = duration;
        this.period = period;
    }

    /**
     * Get the title of this reserved time
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the start time of the first repetition of this reserved time
     * @return start time
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * Get the duration of this reserved time
     * @return duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the period of this reserved time
     * @return period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * Get the number of times this reserved time repeats between the 0 time and endTime
     * @param endTime max time
     * @return number of repetitions
     */
    public int nIntervals(int endTime) {
        return ((endTime - startOffset) / period) + 1;
    }

    /**
     * Construct a list containing all the intervals this recurring reserved time represents up until endTime.
     *
     * @param endTime max time
     * @return list of intervals containing the intervals representing the repetitions of the reserved time up until endTime.
     */
    public List<Interval> intervals(int endTime) {
        int n = this.nIntervals(endTime);
        List<Interval> intervals = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            int start = startOffset + (i * period);
            intervals.add(new Interval(title, start, start + duration));
        }

        return intervals;
    }
}
