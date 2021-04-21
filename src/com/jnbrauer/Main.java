package com.jnbrauer;

import com.jnbrauer.data.Interval;
import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Task[] tasks = {
                new Task("SER222 Honors Contract", 1, 300),
                new Task("SER222 Module 11 Exercise", 1, 60),
                new Task("HST318 Quiz 11", 2, 180),
                new Task("PHY131 HW11", 6, 120),
                new Task("FSE394 Report", 3, 180),
                new Task("FSE394 Presentation", 4, 300),
                new Task("SER334 ADJ2", 3, 300),
                new Task("SER222 Market Analysis", 3, 120),
                new Task("SER222 Module 12 Programming", 3, 180),
                new Task("SER216 Group Assignment", 7, 150)
        };

        ReservedTime[] reservedTimes = {
                new ReservedTime("Sleep", -60, 480, 24*60),
                new ReservedTime("Morning Class", 630, 75, 24*60),
                new ReservedTime("Afternoon Class", 810, 75, 24*60)
        };

        Scheduler scheduler = new Scheduler(7200, tasks, reservedTimes);
        int[][] result = scheduler.run(50);

        // Rank final generation
        Arrays.sort(result, Comparator.comparingInt(scheduler::fitness));
        System.out.println("Best fitness: " + scheduler.fitness(result[0]));

        // Get intervals of best schedule and sort by time
        Interval[] schedule = scheduler.genAllIntervals(result[0]);
        Arrays.sort(schedule);

        for (Interval interval : schedule) {
            System.out.println("--------------------");
            System.out.println("Title: " + interval.getDescription());
            System.out.println("Start time: " + interval.getStart());
            System.out.println("End time: " + interval.getEnd());
            System.out.println("--------------------");
        }
    }
}
