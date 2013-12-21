package com.indivisible.shiftingperspectives.actions;


public abstract class Action
{

    public abstract boolean isActive();

    public abstract boolean triggerAction(long worldTicksTotal);

    protected abstract boolean checkShouldRun(long worldTicksTotal);
}
