package com.indivisible.shiftingperspectives.actions;

/**
 * Extended Action class for use inside the ShiftBorder class.
 * 
 * @author indiv
 * 
 */
public abstract class ShiftSubAction
        extends Action
{

    /**
     * Supply the scheduling for the ShiftBorder Action so the sub-Action can
     * set its own timing relative to those tasks.
     * 
     * @param shiftTiming
     */
    public abstract void setShiftTiming(long[] shiftTiming);
}
