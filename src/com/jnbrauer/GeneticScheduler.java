package com.jnbrauer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Uses a genetic algorithm to schedule a set of tasks.
 *
 * All times are represented as time offsets in seconds. This class always treats 0 as the base time; the caller must
 * handle converting times to have the desired start time offset.
 *
 * Individual schedules are represented as integer arrays where each value represent the start time of the task at the
 * corresponding index in the provided task array.
 */
public class GeneticScheduler {
    // CONSTANTS ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Number of individuals in each generation
    // TODO: make parameter?
    private final int GEN_SIZE = 50;

    // Probability of mutation occurring
    private final double MUTATION_P = 0.5;
    // Mutation normal distribution variance
    private final double MUTATION_VARIANCE = 3600;
    // Selection tournament size
    private final int SELECTION_T = 2;

    // Weight of task overlap in fitness function
    private final double TASK_OVERLAP_WEIGHT = 1;
    // Weight of reserved time overlap in fitness function
    private final double RESERVED_TIME_OVERLAP_WEIGHT = 0.8;

    // INSTANCE VARIABLES //////////////////////////////////////////////////////////////////////////////////////////////
    // Highest possible time value
    private final int maxTime;

    // Task and reserved time data. These should never be modified.
    private final int nTasks;
    private final Task[] tasks;
    private final ReservedTime[] reservedTimes;

    private final Random random;

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
        ReservedTime[] reservedTimes = new ReservedTime[] {};

        GeneticScheduler scheduler = new GeneticScheduler(1000, tasks, reservedTimes);
        int[][] result = scheduler.run(1000);

        // Rank final generation
        Arrays.sort(result, Comparator.comparingInt(scheduler::fitness));
        System.out.println("Best fitness: " + scheduler.fitness(result[0]));

        int[] best = new int[scheduler.nTasks];
        System.arraycopy(result[0], 0, best, 0, scheduler.nTasks);
        for (int i = 0; i < scheduler.nTasks; i++) {
            System.out.println("--------------------");
            System.out.println("Task title: " + tasks[i].title);
            System.out.println("Start time: " + best[i]);
            System.out.println("End time: " + (best[i] + tasks[i].duration));
            System.out.println("--------------------");
        }
    }

    public GeneticScheduler(int maxTime, Task[] tasks, ReservedTime[] reservedTimes) {
        this.maxTime = maxTime;
        this.nTasks = tasks.length;
        this.tasks = tasks;
        this.reservedTimes = reservedTimes;

        this.random = new Random(12);
    }

    public int[][] run(int nGenerations) {
        int[][] currentGen = new int[GEN_SIZE][nTasks];

        // TODO: prevent duplicate individuals
        for (int i = 0; i < GEN_SIZE; i++) {
            currentGen[i] = randomSchedule();
        }

        int n = 0;
        do {
            int[][] nextGen = new int[GEN_SIZE][nTasks];
            for (int i = 0; i < GEN_SIZE / 2; i++) {
                int[] p1 = select(currentGen);
                int[] p2 = select(currentGen);

                int[][] children = crossover(p1, p2);

                nextGen[i*2] = mutate(children[0]);
                nextGen[i*2 + 1] = mutate(children[1]);
            }

            currentGen = nextGen;

            n++;
        } while (n < nGenerations); // TODO: detect when optimal solution has been found

        return currentGen;
    }

    // Tournament selection
    private int[] select(int[][] gen) {
        int[] best = gen[random.nextInt(GEN_SIZE)];

        for (int i = 1; i < SELECTION_T; i++) {
            int[] other = gen[random.nextInt(GEN_SIZE)];
            // By our definition of the fitness function, lower values are better
            if (fitness(other) < fitness(best))
                best = other;
        }

        return best;
    }

    // One-point crossover
    // TODO: consider/test using two-point crossover
    private int[][] crossover(int[] p1, int[] p2) {
        int[] c1 = new int[nTasks];
        int[] c2 = new int[nTasks];

        int crossPoint = random.nextInt(nTasks);

        for (int i = 0; i < nTasks; i++) {
            if (i < crossPoint) {
                c1[i] = p1[i];
                c2[i] = p2[i];
            } else {
                c1[i] = p2[i];
                c2[i] = p1[i];
            }
        }

        return new int[][] {c1, c2};
    }

    private int[] mutate(int[] original) {
        int[] mutated = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            mutated[i] = original[i];
            if (random.nextDouble() <= MUTATION_P) {
                int dt = 0;
                do {
                    dt = (int) (random.nextGaussian() * Math.sqrt(MUTATION_VARIANCE));
                } while (mutated[i] + dt < 0 || mutated[i] + dt > maxTime);
                mutated[i] = mutated[i] + dt;
            }
        }

        return mutated;
    }

    // Calculate fitness function where a lower fitness value represents a better solution (0 is optimal)
    private int fitness(int[] schedule) {
        int fitness = 0;

        // Check for overlaps with other tasks
        int taskOverlap = 0;
        for (int i = 0; i < nTasks; i++) {
            for (int j = 0; j < nTasks; j++) {
                if (i != j) {
                    // Calculate overlap between tasks i and j
                    if (schedule[i] < schedule[j]) {
                        taskOverlap += Math.max(0, (schedule[i] + tasks[i].duration) - schedule[j]);
                    } else {
                        taskOverlap += Math.max(0, (schedule[j] + tasks[j].duration) - schedule[i]);
                    }
                }
            }
        }
        fitness += taskOverlap * TASK_OVERLAP_WEIGHT;

        // Check for overlaps with reserved times
        int reservedOverlap = 0;
        for (int i = 0; i < nTasks; i++) {
            for (ReservedTime reserved : reservedTimes) {
                reservedOverlap += Math.max(0, reserved.duration - ((schedule[i] - reserved.startOffset) % reserved.period));
            }
        }
        fitness += reservedOverlap * RESERVED_TIME_OVERLAP_WEIGHT;

        return fitness;
    }

    private int[] randomSchedule() {
        int[] s = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            s[i] = random.nextInt(maxTime);
        }

        return s;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA STRUCTURES /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Task data
     */
    public static class Task {
        private final String title;
        private final int priority;

        private final int duration;

        public Task(String title, int priority, int duration) {
            this.title = title;
            this.priority = priority;
            this.duration = duration;
        }
    }

    /**
     * Reserved time data
     */
    public static class ReservedTime {
        private final String title;

        private final int startOffset;
        private final int duration;
        private final int period;

        public ReservedTime(String title, int startOffset, int duration, int period) {
            this.title = title;
            this.startOffset = startOffset;
            this.duration = duration;
            this.period = period;
        }
    }
}
