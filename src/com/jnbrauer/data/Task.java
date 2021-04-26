package com.jnbrauer.data;

/**
 * A task that needs to be completed.
 *
 * @author Jude Brauer
 */
public class Task {
    private final String title;
    private final int priority;

    private final int duration;

    /**
     * Create a new task with the given title, priority, and duration.
     * @param title title
     * @param priority priority
     * @param duration duration
     */
    public Task(String title, int priority, int duration) {
        this.title = title;
        this.priority = priority;
        this.duration = duration;
    }

    /**
     * Get the title of this task.
     * @return task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the priority of this task.
     * @return task priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Get the duration of this task.
     * @return task duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the interval that represents this task when it is scheduled at a certain time.
     * @param start time at which to schedule this task
     * @return interval starting at the given start time and with the duration of this task
     */
    public Interval getInterval(int start) {
        return new Interval(title, start, start+duration);
    }
}
