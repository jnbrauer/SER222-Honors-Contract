package com.jnbrauer.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Immutable interval tree for storing time intervals and finding overlap between intervals.
 *
 * @author Jude Brauer
 */
public class IntervalTree {
    /**
     * A node in the interval tree.
     *
     * Contains all the intervals that overlap with this node's center point and references to left and right nodes.
     */
    private static class Node {
        int center;

        Node left;
        Node right;

        // This node's intervals sorted by start time
        Interval[] intervalsStart;
        // This node's intervals sorted by end time
        Interval[] intervalsEnd;

        // Total number of intervals in the tree rooted at this node
        int treeSize;
        // Number of intervals in this node only
        int nodeSize;

        /**
         * Initialize a new node with the given intervals for the given range.
         *
         * Intervals overlapping with the center point of the range defined by min and max will be stored in this node.
         * Left and/or right nodes will recursively be automatically created and initialized for non-overlapping intervals.
         *
         * @param min min value of this node's range
         * @param max max value of this node's range
         * @param intervals intervals to add
         * @param n number of intervals
         */
        public Node(int min, int max, Interval[] intervals, int n) {
            // Find center point
            this.center = (min + max) / 2;
            this.treeSize = n;

            Interval[] centered = new Interval[n];
            Interval[] left = new Interval[n];
            Interval[] right = new Interval[n];

            int c = 0, l = 0, r = 0;
            // Loop until either the end of the list is reached or a null is found
            for (int i = 0; i < n && intervals[i] != null; i++) {
                Interval interval = intervals[i];
                if (this.center >= interval.getStart() && this.center <= interval.getEnd()) {
                    // Add interval to centered list if it overlaps this node's center point
                    centered[c++] = interval;
                } else if (interval.getEnd() < this.center) {
                    // Add interval to left list if it is completely left of center
                    left[l++] = interval;
                } else if (interval.getStart() > this.center) {
                    // Add interval to right list if it is completely right of center
                    right[r++] = interval;
                }
            }

            // Create and sort arrays for this node's interval sorted by start time and end time
            this.intervalsStart = new Interval[c];
            this.intervalsEnd = new Interval[c];
            this.nodeSize = c;

            System.arraycopy(centered, 0, this.intervalsStart, 0, c);
            System.arraycopy(centered, 0, this.intervalsEnd, 0, c);

            Arrays.sort(this.intervalsStart, (a, b) -> Integer.compare(a.getStart(), b.getStart()));
            Arrays.sort(this.intervalsEnd, (a, b) -> Integer.compare(b.getEnd(), a.getEnd()));

            // Initialize left node
            if (l > 0) this.left = new Node(min, this.center, left, l);
            else this.left = null;

            // Initialize right node
            if (r > 0) this.right = new Node(this.center, max, right, r);
            else this.right = null;
        }
    }

    // Root node of tree
    private final Node root;

    /**
     * Initialize a tree from a set of reserved times. Uses [0, endTime] for the range.
     * @param reservedTimes reserved times
     * @param endTime maximum time value
     */
    public IntervalTree(ReservedTime[] reservedTimes, int endTime) {
        List<Interval> intervals = new LinkedList<>();

        for (ReservedTime reserved : reservedTimes) {
            intervals.addAll(reserved.intervals(endTime));
        }

        this.root = new Node(0, endTime, intervals.toArray(new Interval[0]), intervals.size());
    }

    /**
     * Initialize an tree from a set of intervals.
     * @param min minimum time value
     * @param max maximum time value
     * @param intervals intervals
     */
    public IntervalTree(int min, int max, Interval[] intervals) {
        this.root = new Node(min, max, intervals, intervals.length);
    }

    /**
     * Get the total overlap of an interval with the intervals in this tree.
     * @param interval interval
     * @return total overlap with this tree
     */
    public int getOverlap(Interval interval) {
        return getOverlap(root, interval, 0);
    }

    private int getOverlap(Node node, Interval interval, int n) {
        if (node == null) return n;
        else if (interval.getEnd() < node.center) {
            // If this interval ends left of center, check overlap with node's intervals sorted by start
            for (int i = 0; i < node.intervalsStart.length && interval.getEnd() > node.intervalsStart[i].getStart(); i++) {
                n += interval.overlap(node.intervalsStart[i]);
            }

            // Check overlap with left node
            return getOverlap(node.left, interval, n);
        } else if (interval.getStart() > node.center) {
            // If this interval starts right of center, check overlap with node's intervals sorted by end
            for (int i = 0; i < node.intervalsEnd.length && interval.getStart() < node.intervalsEnd[i].getEnd(); i++) {
                n += interval.overlap(node.intervalsEnd[i]);
            }

            // Check overlap with right node
            return getOverlap(node.right, interval, n);
        } else {
            // Interval overlaps center, check with all intervals
            for (int i = 0; i < node.intervalsStart.length; i++) {
                n += interval.overlap(node.intervalsStart[i]);
            }

            // Check overlap with both right and left
            return getOverlap(node.left, interval, n) + getOverlap(node.right, interval, n);
        }
    }

    /**
     * Get all the intervals contained in this tree in order by start time.
     * @return array of intervals in this tree
     */
    public Interval[] getIntervals() {
        Interval[] intervals = new Interval[getSize()];

        getIntervals(root, intervals, 0, getSize());

        return intervals;
    }

    // Perform in-order traversal of tree
    private void getIntervals(Node node, Interval[] array, int start, int end) {
        // Put left node intervals in array starting at start
        int div1 = start;
        if (node.left != null) {
            div1 = start + node.left.treeSize;
            getIntervals(node.left, array, start, div1);
        }

        // Get start point of right intervals sections in array
        int div2 = div1 + node.nodeSize;

        // Add this node's interval to the array in the range [div1, div2)
        for (int i = div1; i < div2; i++) {
            array[i] = node.intervalsStart[i - div1];
        }

        // Add right node intervals in array in range [div2, end)
        if (node.right != null) {
            getIntervals(node.right, array, div2, end);
        }
    }

    /**
     * Get the number of intervals in this tree.
     * @return total number of intervals
     */
    public int getSize() {
        return root.treeSize;
    }
}
