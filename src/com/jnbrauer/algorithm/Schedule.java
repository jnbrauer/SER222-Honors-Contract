package com.jnbrauer.algorithm;

import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Schedule {
    private List<Task> tasks;
    private List<ReservedTime> reserved;

    public Schedule(List<Task> tasks, List<ReservedTime> reserved) {
        this.tasks = new ArrayList<>(tasks.size());
        this.reserved = new ArrayList<>(reserved.size());

        Collections.copy(this.tasks, tasks);
        Collections.copy(this.reserved, reserved);
    }

    public void randomize() {
        randomize(new Random());
    }

    /**
     * Schedule the tasks in this schedule at random times
     * @param random
     */
    public void randomize(Random random) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.set(i, tasks.get(i).schedule(random.nextInt()));
        }
    }

    public Schedule cross(Schedule other) {
        // TODO: cross this schedule with another schedule
        return null;
    }

    public int getFitness() {
        // TODO: evaluate the fitness function for this schedule
        return 0;
    }
}
