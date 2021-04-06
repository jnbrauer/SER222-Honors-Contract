package com.jnbrauer;

import java.util.Arrays;

public class IntervalTree {
    private class Node {
        int center;

        Node left;
        Node right;

        Interval[] intervalsStart;
        Interval[] intervalsEnd;

        public Node(int min, int max, Interval[] intervals, int n) {
            this.center = (min + max) / 2;

            Interval[] centered = new Interval[n];
            Interval[] left = new Interval[n];
            Interval[] right = new Interval[n];

            int c = 0, l = 0, r = 0;
            for (int i = 0; i < n && intervals[i] != null; i++) {
                Interval interval = intervals[i];
                if (this.center > interval.getStart() && this.center < interval.getEnd()) {
                    centered[c++] = interval;
                } else if (interval.getEnd() < this.center) {
                    left[l++] = interval;
                } else if (interval.getStart() > this.center) {
                    right[r++] = interval;
                }
            }

            this.intervalsStart = new Interval[c];
            this.intervalsEnd = new Interval[c];

            System.arraycopy(centered, 0, this.intervalsStart, 0, c);
            System.arraycopy(centered, 0, this.intervalsEnd, 0, c);

            Arrays.sort(this.intervalsStart, (a, b) -> Integer.compare(a.getStart(), b.getStart()));
            Arrays.sort(this.intervalsEnd, (a, b) -> Integer.compare(b.getEnd(), a.getEnd()));

            if (l > 0) this.left = new Node(min, this.center, left, l);
            else this.left = null;

            if (r > 0) this.right = new Node(this.center, max, right, r);
            else this.right = null;
        }
    }

    public static class Interval {
        private final int start;
        private final int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int overlap(Interval other) {
            return Math.max(0, Math.min(this.end, other.end) - Math.max(this.start, other.start));
        }
    }

    private final Node root;

    public IntervalTree(int min, int max, Interval[] intervals) {
        this.root = new Node(min, max, intervals, intervals.length);
    }

    public int getOverlap(Interval interval) {
        return getOverlap(root, interval, 0);
    }

    private int getOverlap(Node node, Interval interval, int n) {
        if (node == null) return n;
        else if (interval.getEnd() < node.center) {
            for (int i = 0; i < node.intervalsStart.length && interval.getEnd() > node.intervalsStart[i].getStart(); i++) {
                n += interval.overlap(node.intervalsStart[i]);
            }

            return getOverlap(node.left, interval, n);
        } else if (interval.getStart() > node.center) {
            for (int i = 0; i < node.intervalsEnd.length && interval.getStart() < node.intervalsEnd[i].getEnd(); i++) {
                n += interval.overlap(node.intervalsEnd[i]);
            }

            return getOverlap(node.right, interval, n);
        } else {
            for (int i = 0; i < node.intervalsStart.length; i++) {
                n += interval.overlap(node.intervalsStart[i]);
            }

            return getOverlap(node.left, interval, n) + getOverlap(node.right, interval, n);
        }
    }
}
