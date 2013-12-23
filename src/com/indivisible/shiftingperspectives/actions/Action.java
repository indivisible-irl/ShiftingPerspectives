package com.indivisible.shiftingperspectives.actions;

/**
 * Abstract Parent Class for plugin Actions
 * 
 * @author indiv
 */
public abstract class Action
{

    /**
     * Checks if Action is enabled in the configuration.
     * 
     * @return
     */
    public abstract boolean isEnabled();

    /**
     * Checks if the Action is currently active and has a task in the
     * scheduler.
     * 
     * @return
     */
    public abstract boolean isActive();

    /**
     * Starts the Action's task and adds it to the scheduler if applicable.
     * 
     * @return
     */
    public abstract boolean start();

    /**
     * Stops any queued task the Action may have in the scheduler.
     * 
     * @return
     */
    public abstract boolean stop();

    /**
     * Resets and tasks the Action may have running. Called when time changes
     * or moves out of sync.
     * 
     * @return
     */
    public abstract boolean reset();
}
