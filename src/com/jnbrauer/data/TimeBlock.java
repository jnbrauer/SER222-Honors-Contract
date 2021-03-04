package com.jnbrauer.data;

public abstract class TimeBlock {
    public abstract int getDuration();
    public abstract int getStartTime();

    /**
     * Get the amount of overlap between this TimeBlock and another TimeBlock
     * @param other
     * @return
     */
    public int getOverlap(TimeBlock other) {
        if (this.getStartTime() < other.getStartTime()) {
            return Math.max(0, (this.getStartTime() + this.getDuration()) - other.getStartTime());
        } else {
            return Math.max(0, (other.getStartTime() + other.getDuration()) - this.getStartTime());
        }
    }
}
