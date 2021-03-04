package com.jnbrauer.algorithm;

import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Generation {
    private final List<Schedule> schedules;

    /**
     * Create the initial generation based on list of tasks and reserved times
     * @param size number of schedules to create for generation
     * @param tasks
     * @param reservedTimes
     */
    public Generation(int size, List<Task> tasks, List<ReservedTime> reservedTimes) {
        this.schedules = new ArrayList<>(size);
    }

    /**
     * Create a generation with a starting set of schedules
     * @param schedules
     */
    public Generation(List<Schedule> schedules) {
        this.schedules = new ArrayList<>(schedules.size());
        Collections.copy(this.schedules, schedules);
    }

    public List<Schedule> rankSchedules() {
        // TODO: rank schedules according to fitness function and select schedules to use in next generation
        return schedules;
    }

    public Generation createNext() {
        // TODO: create next generation by crossing schedules returned by rankSchedules()
        return null;
    }
}
