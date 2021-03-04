package com.jnbrauer.algorithm;

import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.List;

public class Scheduler {
    private List<Task> tasks;
    private List<ReservedTime> reserved;

    public Scheduler() {

    }

    public void loadSchedule(String file) {
        // TODO: load tasks, reserved times, etc. from file
    }

    public Schedule findBestSchedule() {
        // TODO: Run genetic algorithm
        return null;
    }
}
