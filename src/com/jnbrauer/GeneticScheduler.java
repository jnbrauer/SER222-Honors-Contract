package com.jnbrauer;

import com.jnbrauer.data.Interval;
import com.jnbrauer.data.IntervalTree;
import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;

import java.util.Random;

/**
 * Uses a genetic algorithm to schedule a set of tasks.
 *
 * All times are represented as time offsets in minutes. This class always treats 0 as the base time; the caller must
 * handle converting times to have the desired start time offset.
 *
 * Individual schedules are represented as integer arrays where each value represent the start time of the task at the
 * corresponding index in the provided task array.
 */
public class GeneticScheduler {
    // CONSTANTS ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Number of individuals in each generation
    private final int GEN_SIZE = 50;

    // Probability of mutation occurring
    private final double MUTATION_P = 0.5;
    // Mutation normal distribution standard deviation
    private final double MUTATION_STDDEV = 10;
    // Selection tournament size
    private final int SELECTION_T = 2;

    // Weight of task overlap in fitness function
    private final double TASK_OVERLAP_WEIGHT = 1;
    // Weight of reserved time overlap in fitness function
    private final double RESERVED_TIME_OVERLAP_WEIGHT = 0.8;

    // INSTANCE VARIABLES //////////////////////////////////////////////////////////////////////////////////////////////
    // Highest possible time value
    private final int maxTime;

    // Task and reserved time data. These should never be modified after initialization.
    private final int nTasks;
    private final Task[] tasks;
    private final ReservedTime[] reservedTimes;
    private final IntervalTree reservedIntervals;

    private final Random random;

    public GeneticScheduler(int maxTime, Task[] tasks, ReservedTime[] reservedTimes) {
        this.maxTime = maxTime;
        this.nTasks = tasks.length;
        this.tasks = tasks;
        this.reservedTimes = reservedTimes;
        this.reservedIntervals = new IntervalTree(reservedTimes, maxTime);

        this.random = new Random(12);
    }

    // Run the given number of generations of the genetic algorithm and return the final gneration
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
                // Select two parents
                int[] p1 = select(currentGen);
                int[] p2 = select(currentGen);

                // Cross them to get two children
                int[][] children = crossover(p1, p2);

                // Mutate children and add to next generation
                nextGen[i*2] = mutate(children[0]);
                nextGen[i*2 + 1] = mutate(children[1]);
            }

            currentGen = nextGen;

            n++;
        } while (n < nGenerations); // TODO: detect when optimal solution has been found

        return currentGen;
    }

    // Tournament selection
    // Choose SELECTION_T random individuals and pick the best from those
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
            // Swap all values after the cross point
            if (i >= crossPoint) {
                c1[i] = p2[i];
                c2[i] = p1[i];
            } else {
                c1[i] = p1[i];
                c2[i] = p2[i];
            }
        }

        return new int[][] {c1, c2};
    }

    private int[] mutate(int[] original) {
        int[] mutated = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            mutated[i] = original[i];
            // MUTATION_P chance of a mutation occurring
            if (random.nextDouble() <= MUTATION_P) {
                int dt = 0;
                do {
                    // Normally distributed mutation with given standard deviation
                    dt = (int) (random.nextGaussian() * MUTATION_STDDEV);
                } while (mutated[i] + dt < 0 || mutated[i] + dt > maxTime); // Loop until valid mutation is found
                mutated[i] = mutated[i] + dt;
            }
        }

        return mutated;
    }

    // Calculate fitness function where a lower fitness value represents a better solution (0 is optimal)
    public int fitness(int[] schedule) {
        int fitness = 0;

        // Check for overlaps with other tasks
        int taskOverlap = 0;
        for (int i = 0; i < nTasks - 1; i++) {
            Interval t1 = new Interval(schedule[i], schedule[i] + tasks[i].getDuration());
            for (int j = i + 1; j < nTasks; j++) {
                // Calculate overlap between tasks i and j
                Interval t2 = new Interval(schedule[j], schedule[j] + tasks[j].getDuration());
                taskOverlap += t1.overlap(t2);
            }
        }
        fitness += taskOverlap * TASK_OVERLAP_WEIGHT;

        // Check for overlaps with reserved times
        int reservedOverlap = 0;
        for (int i = 0; i < nTasks; i++) {
            reservedOverlap += reservedIntervals.getOverlap(new Interval(schedule[i], schedule[i] + tasks[i].getDuration()));
        }
        fitness += reservedOverlap * RESERVED_TIME_OVERLAP_WEIGHT;

        return fitness;
    }

    // Generate a schedule with random start times
    private int[] randomSchedule() {
        int[] s = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            s[i] = random.nextInt(maxTime);
        }

        return s;
    }
}
