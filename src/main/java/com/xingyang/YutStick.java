package com.xingyang;

/**
 * A dice stick used in the Yut game (one of the four sticks)
 */
public class YutStick {
    private boolean flatSide; // true indicates flat side (scoring side, 1 point), false indicates rounded side (back, 0 point)
    
    public YutStick() {
        this.flatSide = false;
    }
    
    /**
     * Toss the stick
     * @return true if flat side is up
     */
    public boolean toss() {
        // 60% chance to land on the flat side (scoring)
        flatSide = Math.random() < 0.6;
        return flatSide;
    }
    
    /**
     * Get the current facing side
     * @return true if flat side is up
     */
    public boolean isFlatSide() {
        return flatSide;
    }
    
    /**
     * Set the stick's facing side
     * @param flatSide true if flat side is up
     */
    public void setFlatSide(boolean flatSide) {
        this.flatSide = flatSide;
    }
}
