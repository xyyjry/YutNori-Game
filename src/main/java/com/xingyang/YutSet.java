package src.main.java.com.xingyang;

/**
 * A set of four sticks used as dice in the Yut game
 */
public class YutSet {
    // Number of Yut sticks (traditionally 4)
    private static final int STICK_COUNT = 4;
    
    // Collection of Yut sticks
    private YutStick[] sticks;
    
    // Yut result
    public enum YutResult {
        DO(1, "도(Do)"),       // 1 flat side up
        GE(2, "개(Gae)"),      // 2 flat sides up
        GEOL(3, "걸(Geol)"),   // 3 flat sides up
        YUT(4, "윷(Yut)"),     // 4 flat sides up
        MO(5, "모(Mo)"),       // 0 flat sides up (all back sides)
        BACK_DO(-1, "빽도(Back Do)"); // Special case, internal use only
        
        private final int steps;
        private final String name;
        
        YutResult(int steps, String name) {
            this.steps = steps;
            this.name = name;
        }
        
        public int getSteps() {
            return steps;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public YutSet() {
        sticks = new YutStick[STICK_COUNT];
        for (int i = 0; i < STICK_COUNT; i++) {
            sticks[i] = new YutStick();
        }
    }
    
    /**
     * Toss all sticks
     * @return the result of the toss
     */
    public YutResult toss() {
        int flatCount = 0;
        
        for (YutStick stick : sticks) {
            if (stick.toss()) {
                flatCount++;
            }
        }
        
        // Return result based on the number of flat sides up
        switch (flatCount) {
            case 1:
                // Check for special case (Back Do - only first stick is flat side)
                if (sticks[0].isFlatSide() && !sticks[1].isFlatSide() 
                    && !sticks[2].isFlatSide() && !sticks[3].isFlatSide()) {
                    return YutResult.BACK_DO;
                }
                return YutResult.DO;
            case 2:
                return YutResult.GE;
            case 3:
                return YutResult.GEOL;
            case 4:
                return YutResult.YUT;
            case 0:
                return YutResult.MO;
            default:
                throw new IllegalStateException("Invalid flat count: " + flatCount);
        }
    }
    
    /**
     * Get the current states of all sticks
     * @return array of sticks
     */
    public YutStick[] getSticks() {
        return sticks;
    }
}
