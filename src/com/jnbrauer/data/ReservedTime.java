package com.jnbrauer.data;

import java.util.LinkedList;
import java.util.List;

public class ReservedTime {
    private final String title;

    private final int startOffset;
    private final int duration;
    private final int period;

    public ReservedTime(String title, int startOffset, int duration, int period) {
        this.title = title;
        this.startOffset = startOffset;
        this.duration = duration;
        this.period = period;
    }

    public String getTitle() {
        return title;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getDuration() {
        return duration;
    }

    public int getPeriod() {
        return period;
    }

    public int nIntervals(int endTime) {
        return ((endTime - startOffset) / period) + 1;
    }

    // Construct a list containing all the intervals this recurring reserved time represents
    public List<Interval> intervals(int endTime) {
        int n = this.nIntervals(endTime);
        List<Interval> intervals = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            int start = startOffset + (i * period);
            intervals.add(new Interval(start, start + duration));
        }

        return intervals;
    }
}
