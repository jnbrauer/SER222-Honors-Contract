package com.jnbrauer.data;

public class ReservedTime extends TimeBlock {
    private final String name;
    private final int duration;
    private final int startTime;
    private final int priority;

    public ReservedTime(String name, int duration, int startTime, int priority) {
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
        this.priority = priority;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
