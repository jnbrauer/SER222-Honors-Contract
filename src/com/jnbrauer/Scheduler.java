package com.jnbrauer;

import com.jnbrauer.data.Interval;
import com.jnbrauer.data.IntervalTree;
import com.jnbrauer.data.ReservedTime;
import com.jnbrauer.data.Task;
import com.jnbrauer.utils.CSVWriter;

import java.io.IOException;
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
public class Scheduler {
    // CONSTANTS ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Number of individuals in each generation
    private static final int GEN_SIZE = 50;

    // Probability of mutation occurring
    private static final double MUTATION_P = 0.3;
    // Mutation normal distribution standard deviation
    private static final double MUTATION_STDDEV = 60;
    // Selection tournament size
    private static final int SELECTION_T = 2;

    // Weight of task overlap in fitness function
    private static final double TASK_OVERLAP_WEIGHT = 1;
    // Weight of reserved time overlap in fitness function
    private static final double RESERVED_TIME_OVERLAP_WEIGHT = 0.3;

    private static final double PRIORITY_WEIGHT = 100;

    // INSTANCE VARIABLES //////////////////////////////////////////////////////////////////////////////////////////////
    // Highest possible time value
    private final int maxTime;

    // Task and reserved time data. These should never be modified after initialization.
    private final int nTasks;
    private final Task[] tasks;
    private final ReservedTime[] reservedTimes;
    private final IntervalTree reservedIntervals;

    private final Random random;

    public Scheduler(int maxTime, Task[] tasks, ReservedTime[] reservedTimes) {
        this.maxTime = maxTime;
        this.nTasks = tasks.length;
        this.tasks = tasks;
        this.reservedTimes = reservedTimes;
        this.reservedIntervals = new IntervalTree(reservedTimes, maxTime);

        this.random = new Random(12);
    }

    // Run the given number of generations of the genetic algorithm and return the final generation
    public int[][] run(int nGenerations) {
        // Create CSV writer and construct header
        CSVWriter csv = new CSVWriter("output.csv");
        String[] headerLine = new String[nTasks + 2];
        headerLine[0] = "BestFitness";
        headerLine[1] = "AvgFitness";
        for (int i = 0; i < nTasks; i++) headerLine[i + 2] = tasks[i].getTitle();
        csv.addLine(headerLine);

        int[][] currentGen = new int[GEN_SIZE][nTasks];

        // TODO: prevent duplicate individuals
        for (int i = 0; i < GEN_SIZE; i++) {
            currentGen[i] = randomSchedule();
        }

        int n = 0;
        do {
            // Calculate all fitnesses
            int[] fitnesses = new int[GEN_SIZE];
            for (int i = 0; i < GEN_SIZE; i++) fitnesses[i] = fitness(currentGen[i]);

            // Find best fitness
            int bestFitness = fitnesses[0];
            int bestIndex = 0;
            for (int i = 1; i < GEN_SIZE; i++) {
                if (fitnesses[i] < bestFitness) {
                    bestFitness = fitnesses[i];
                    bestIndex = i;
                }
            }

            // Calculate average fitness
            int fitnessSum = 0;
            for (int i = 0; i < GEN_SIZE; i++) fitnessSum += fitnesses[i];
            double avgFitness = (double) fitnessSum / GEN_SIZE;

            // Write fitness statistics and most fit schedule to log file
            String[] newLine = new String[nTasks + 2];
            newLine[0] = String.valueOf(bestFitness);
            newLine[1] = String.valueOf(avgFitness);
            for (int i = 0; i < nTasks; i++) newLine[i + 2] = String.valueOf(currentGen[bestIndex][i]);
            csv.addLine(newLine);

            // Generate next generation
            int[][] nextGen = new int[GEN_SIZE][nTasks];
            for (int i = 0; i < GEN_SIZE / 2; i++) {
                // Select two parents
                int[] p1 = currentGen[select(fitnesses)];
                int[] p2 = currentGen[select(fitnesses)];

                // Cross them to get two children
                int[][] children = crossover(p1, p2);

                // Mutate children and add to next generation
                nextGen[i*2] = mutate(children[0]);
                nextGen[i*2 + 1] = mutate(children[1]);
            }

            currentGen = nextGen;

            n++;
        } while (n < nGenerations); // TODO: detect when optimal solution has been found

        try {
            csv.write();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentGen;
    }

    // Tournament selection
    // Choose SELECTION_T random individuals and pick the best from those
    // Returns the index of the selected individual based on an array of fitnesses
    private int select(int[] fitnesses) {
        int best = random.nextInt(GEN_SIZE);

        for (int i = 1; i < SELECTION_T; i++) {
            int other = random.nextInt(GEN_SIZE);
            // By our definition of the fitness function, lower values are better
            if (fitnesses[other] < fitnesses[best])
                best = other;
        }

        return best;
    }

    // Two-point crossover
    // Two-point seems to provide better performance when priorities are considered
    private int[][] crossover(int[] p1, int[] p2) {
        int[] c1 = new int[nTasks];
        int[] c2 = new int[nTasks];

        int crossPoint1 = random.nextInt(nTasks);
        int crossPoint2 = random.nextInt(nTasks - crossPoint1) + crossPoint1;

        for (int i = 0; i < nTasks; i++) {
            // Swap all values after the cross point
            if (i >= crossPoint1 && i <= crossPoint2) {
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

        Interval[] intervals = genTaskIntervals(schedule);

        // Check for overlaps with other tasks
        int taskOverlap = 0;
        int priorityInversions = 0;

        for (int i = 0; i < nTasks - 1; i++) {
            for (int j = i + 1; j < nTasks; j++) {
                // Calculate overlap between tasks i and j
                taskOverlap += intervals[i].overlap(intervals[j]);

                if ((schedule[i] < schedule[j] && tasks[i].getPriority() > tasks[j].getPriority())
                        || (schedule[i] > schedule[j] && tasks[i].getPriority() < tasks[j].getPriority())) {
                    priorityInversions += Math.abs(tasks[i].getPriority() - tasks[j].getPriority());
                }
            }
        }
        fitness += taskOverlap * TASK_OVERLAP_WEIGHT;
        fitness += priorityInversions * PRIORITY_WEIGHT;

        // Check for overlaps with reserved times
        int reservedOverlap = 0;
        for (int i = 0; i < nTasks; i++) {
            reservedOverlap += reservedIntervals.getOverlap(intervals[i]);
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

    private Interval[] genTaskIntervals(int[] schedule) {
        Interval[] intervals = new Interval[nTasks];

        for (int i = 0; i < nTasks; i++) {
            intervals[i] = tasks[i].getInterval(schedule[i]);
        }

        return intervals;
    }

    public Interval[] genAllIntervals(int[] schedule) {
        Interval[] intervals = new Interval[nTasks + reservedIntervals.getSize()];

        for (int i = 0; i < nTasks; i++) {
            intervals[i] = tasks[i].getInterval(schedule[i]);
        }

        if (reservedIntervals.getSize() >= 0)
            System.arraycopy(reservedIntervals.getIntervals(), 0, intervals, nTasks, reservedIntervals.getSize());

        return intervals;
    }
}