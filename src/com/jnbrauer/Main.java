package com.jnbrauer;

import com.jnbrauer.data.Interval;
import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Task[] tasks = new Task[] {
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

        ReservedTime[] reservedTimes = new ReservedTime[] {
                new ReservedTime("Sleep", 1320, 480, 1440),
                new ReservedTime("Morning Class", 630, 75, 1440),
                new ReservedTime("Afternoon Class", 810, 75, 1440)
        };

        GeneticScheduler scheduler = new GeneticScheduler(7200, tasks, reservedTimes);
        int[][] result = scheduler.run(500);

        // Rank final generation
        Arrays.sort(result, Comparator.comparingInt(scheduler::fitness));
        System.out.println("Best fitness: " + scheduler.fitness(result[0]));

        Interval[] schedule = scheduler.genAllIntervals(result[0]);
        Arrays.sort(schedule);

        for (int i = 0; i < schedule.length; i++) {
            System.out.println("--------------------");
            System.out.println("Title: " + schedule[i].getDescription());
            System.out.println("Start time: " + schedule[i].getStart());
            System.out.println("End time: " + schedule[i].getEnd());
            System.out.println("--------------------");
        }

        /*int[] best = new int[result[0].length];
        System.arraycopy(result[0], 0, best, 0, best.length);
        for (int i = 0; i < best.length; i++) {
            System.out.println("--------------------");
            System.out.println("Task title: " + tasks[i].getTitle());
            System.out.println("Start time: " + best[i]);
            System.out.println("End time: " + (best[i] + tasks[i].getDuration()));
            System.out.println("--------------------");
        }*/
    }
}
