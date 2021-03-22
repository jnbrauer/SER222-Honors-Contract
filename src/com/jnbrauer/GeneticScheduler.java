package com.jnbrauer; import java.util.Random;

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
    private int totalDuration;

    // Task and reserved time data. These should never be modified.
    private final int nTasks;
    private final Task[] tasks;
    private final ReservedTime[] reservedTimes;

    private Random random;

    public GeneticScheduler(int totalDuration, Task[] tasks, ReservedTime[] reservedTimes) {
        this.totalDuration = totalDuration;
        this.nTasks = tasks.length;
        this.tasks = tasks;
        this.reservedTimes = reservedTimes;

        this.random = new Random(12);
    }

    public void run() {
        int[][] currentGen = new int[GEN_SIZE][nTasks];

        // TODO: prevent duplicate individuals
        for (int i = 0; i < nTasks; i++) {
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
        } while (n < 100); // TODO: detect when optimal solution has been found
    }

    // Tournament selection
    private int[] select(int[][] gen) {
        int[] a = gen[random.nextInt(GEN_SIZE)];

        for (int i = 1; i < SELECTION_T; i++) {
            int[] b = gen[random.nextInt(GEN_SIZE)];
            if (fitness(b) < fitness(a))
                a = b;
        }

        return a;
    }

    // One-point crossover
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

    private int[] mutate(int[] a) {
        int[] s = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            s[i] = a[i];
            if (random.nextDouble() <= MUTATION_P) {
                int n = 0;
                do {
                    n = (int) (random.nextGaussian() * Math.sqrt(MUTATION_VARIANCE));
                } while (s[i] + n < 0 || s[i] + n > totalDuration);
                s[i] = s[i] + n;
            }
        }

        return s;
    }

    // Calculate fitness function where a lower fitness value represents a better solution (0 is optimal)
    private int fitness(int[] s) {
        int f = 0;

        // Check for overlaps with other tasks
        int taskOverlap = 0;
        for (int i = 0; i < nTasks; i++) {
            for (int j = 0; j < nTasks; j++) {
                if (i != j)
                  taskOverlap += tasks[i].overlap(tasks[j]);
            }
        }
        f += taskOverlap * TASK_OVERLAP_WEIGHT;

        // Check for overlaps with reserved times

        return f;
    }

    private int[] randomSchedule() {
        int[] s = new int[nTasks];

        for (int i = 0; i < nTasks; i++) {
            s[i] = random.nextInt(totalDuration);
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

        private final int start;
        private final int duration;

        public Task(String title, int priority, int start, int duration) {
            this.title = title;
            this.priority = priority;
            this.start = start;
            this.duration = duration;
        }

        public int overlap(Task other) {
            if (this.start < other.start) {
                return Math.max(0, (this.start + this.duration) - other.start);
            } else {
                return Math.max(0, (other.start + other.duration) - this.start);
            }
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

        public int overlap(Task t) {
            return Math.max(0, duration - ((t.start - startOffset) % period));
        }
    }

}
