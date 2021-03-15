package com.jnbrauer;

import com.jnbrauer.algorithm.Schedule;
import com.jnbrauer.algorithm.Scheduler;

public class Main {

    /**
     * see what people think of schedules
     *
     * read more about how others are using genetic algorithms and representing data
     *
     * interval trees
     *
     * meet around 18th, some output (random schedules)
     *
     * @param args
     */
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        scheduler.loadSchedule("schedule.json");
        Schedule best = scheduler.findBestSchedule();
        System.out.println(best);
    }
}
