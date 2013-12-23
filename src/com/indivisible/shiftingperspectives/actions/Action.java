package com.indivisible.shiftingperspectives.actions;


public abstract class Action
{

    public abstract boolean isEnabled();

    public abstract boolean isActive();

    public abstract boolean start();

    public abstract boolean stop();

    public abstract boolean reset();
}
