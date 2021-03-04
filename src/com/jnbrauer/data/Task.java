package com.jnbrauer.data;

public class Task extends TimeBlock {
    private final String name;
    private final int duration;
    private final int deadline;
    private final int priority;

    private final boolean scheduled;
    private final int startTime;

    public Task(String name, int duration, int deadline, int priority, int startTime) {
        this.name = name;
        this.duration = duration;
        this.deadline = deadline;
        this.priority = priority;

        this.scheduled = startTime >= 0;
        this.startTime = startTime;
    }

    public Task(String name, int duration, int deadline, int priority) {
        this(name, duration, deadline, priority, -1);
    }

    /**
     * Copies a tasks and schedules it at the given time
     * @param t
     * @param startTime
     */
    public Task(Task t, int startTime) {
        this(t.getName(), t.getDuration(), t.getDeadline(), t.getPriority(), startTime);
    }

    /**
     * Copy constructor
     * @param t
     */
    public Task(Task t) {
        this(t, t.getStartTime());
    }

    /**
     * Creates a copy of this tasks that is scheduled at the given time
     * @param startTime
     * @return
     */
    public Task schedule(int startTime) {
        return new Task(this, startTime);
    }

    public String getName() {
        return name;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return String.format("Task: [name=%s  duration=%d  deadline=%d  priority=%d  scheduled=%b  startTime=%d]",
                name,
                duration,
                deadline,
                priority,
                scheduled,
                startTime);
    }
}
