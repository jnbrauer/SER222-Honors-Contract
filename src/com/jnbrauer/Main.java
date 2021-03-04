package com.jnbrauer;

import com.jnbrauer.algorithm.Schedule;
import com.jnbrauer.algorithm.Scheduler;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        scheduler.loadSchedule("schedule.json");
        Schedule best = scheduler.findBestSchedule();
        System.out.println(best);
    }
}
