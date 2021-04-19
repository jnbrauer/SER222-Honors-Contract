package com.jnbrauer.data;

public class Task {
    private final String title;
    private final int priority;

    private final int duration;

    public Task(String title, int priority, int duration) {
        this.title = title;
        this.priority = priority;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public int getDuration() {
        return duration;
    }

    public Interval getInterval(int start) {
        return new Interval(title, start, start+duration);
    }
}
