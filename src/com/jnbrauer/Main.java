package com.jnbrauer;

import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Task t1 = new Task("Test 1", 0, 20);
        Task t2 = new Task("Test 2", 0, 10);
        Task t3 = new Task("Test 3", 0, 60);
        Task t4 = new Task("Test 4", 0, 120);
        Task t5 = new Task("Test 5", 0, 40);
        Task t6 = new Task("Test 6", 0, 300);
        Task t7 = new Task("Test 7", 0, 5);
        Task t8 = new Task("Test 8", 0, 200);
        Task t9 = new Task("Test 9", 0, 36);
        Task t10 = new Task("Test 10", 0, 67);

        Task[] tasks = new Task[] {t1, t2, t3, t4, t5, t6, t7, t8, t9, t10};

        ReservedTime r1 = new ReservedTime("Sleep", 20, 30, 100);

        ReservedTime[] reservedTimes = new ReservedTime[] {r1};

        GeneticScheduler scheduler = new GeneticScheduler(1000, tasks, reservedTimes);
        int[][] result = scheduler.run(1000);

        // Rank final generation
        Arrays.sort(result, Comparator.comparingInt(scheduler::fitness));
        System.out.println("Best fitness: " + scheduler.fitness(result[0]));

        int[] best = new int[result[0].length];
        System.arraycopy(result[0], 0, best, 0, best.length);
        for (int i = 0; i < best.length; i++) {
            System.out.println("--------------------");
            System.out.println("Task title: " + tasks[i].getTitle());
            System.out.println("Start time: " + best[i]);
            System.out.println("End time: " + (best[i] + tasks[i].getDuration()));
            System.out.println("--------------------");
        }
    }

}
