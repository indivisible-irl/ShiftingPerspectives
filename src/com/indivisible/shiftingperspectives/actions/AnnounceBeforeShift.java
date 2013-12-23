package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;


public class AnnounceBeforeShift
        extends Action
{

    //// data

    private JavaPlugin plugin;

    private boolean doAnnounceBeforeShift;
    private long announceBeforeShiftTime;
    private String announceBeforeShiftMessage;

    private static String CFG_ANNOUNCE_ACTIVE = "settings.announce.warn_before_shift.active";
    private static String CFG_ANNOUNCE_TIME = "settings.announce.warn_before_shift.ticks_before";
    private static String CFG_ANNOUNCE_MESSAGE = "settings.announce.warn_before_shift.message";

    private static long DEFAULT_TIME_BEFORE = 2400L;
    private static String DEFAULT_ANNOUNCE_MESSAGE = "Border shift soon. Grab your gear and move out!!";

    //// constructors & init

    public AnnounceBeforeShift(JavaPlugin jPlugin)
    {
        this.doAnnounceBeforeShift = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE,
                                                                    false);
        if (doAnnounceBeforeShift)
        {
            init(jPlugin);
        }
    }

    private void init(JavaPlugin jPlugin)
    {
        this.plugin = jPlugin;
        this.announceBeforeShiftTime = plugin.getConfig().getLong(CFG_ANNOUNCE_TIME,
                                                                  DEFAULT_TIME_BEFORE);
        this.announceBeforeShiftMessage = plugin.getConfig()
                .getString(CFG_ANNOUNCE_MESSAGE, DEFAULT_ANNOUNCE_MESSAGE);
    }


    //// test if & when should run

    @Override
    public boolean isActive()
    {
        return doAnnounceBeforeShift;
    }


    @Override
    protected boolean checkShouldRun(long worldTicksTotal)
    {
        // TODO Need to have Shift Action created first to pass in here.
        return false;
    }


    //// action

    @Override
    public boolean triggerAction(long worldTicksTotal)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
