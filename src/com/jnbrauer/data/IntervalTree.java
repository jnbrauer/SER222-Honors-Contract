package com.jnbrauer.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IntervalTree {
    private class Node {
        int center;

        Node left;
        Node right;

        Interval[] intervalsStart;
        Interval[] intervalsEnd;

        int treeSize;
        int nodeSize;

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

    private final Node root;

    public IntervalTree(ReservedTime[] reservedTimes, int endTime) {
        List<Interval> intervals = new LinkedList<>();

        for (ReservedTime reserved : reservedTimes) {
            intervals.addAll(reserved.intervals(endTime));
        }

        this.root = new Node(0, endTime, intervals.toArray(new Interval[0]), intervals.size());
    }

    public IntervalTree(int min, int max, Interval[] intervals) {
        this.root = new Node(min, max, intervals, intervals.length);
    }

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

    public Interval[] getIntervals() {
        Interval[] intervals = new Interval[getSize()];

        getIntervals(root, intervals, 0, getSize());

        return intervals;
    }

    public void getIntervals(Node node, Interval[] array, int start, int end) {
        int div1 = start;
        if (node.left != null) {
            div1 = start + node.left.treeSize;
            getIntervals(node.left, array, start, div1);
        }

        int div2 = div1 + node.nodeSize;

        for (int i = div1; i < div2; i++) {
            array[i] = node.intervalsStart[i - div1];
        }

        if (node.right != null) {
            getIntervals(node.right, array, div2, end);
        }
    }

    public int getSize() {
        return root.treeSize;
    }
}
