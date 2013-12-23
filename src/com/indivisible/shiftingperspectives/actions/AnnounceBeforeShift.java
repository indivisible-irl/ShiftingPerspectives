package com.indivisible.shiftingperspectives.actions;

import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.ShiftingPerspectives;

/**
 * Action class to announce server-wide that a Shift is to take place a set
 * time before it is to occur.
 * 
 * @author indiv
 * 
 */
public class AnnounceBeforeShift
        extends ShiftSubAction
{

    //// data

    private ShiftingPerspectives plugin;
    private int taskID = 0;
    private long[] shiftTiming = null;

    private boolean doAnnounceBeforeShift;
    private long announceBeforeShiftTime;
    private String announceBeforeShiftMessage;

    private static String CFG_ANNOUNCE_ACTIVE = "settings.announce.warn_before_shift.active";
    private static String CFG_ANNOUNCE_TIME = "settings.announce.warn_before_shift.ticks_before";
    private static String CFG_ANNOUNCE_MESSAGE = "settings.announce.warn_before_shift.message";

    private static long DEFAULT_TICKS_BEFORE = 2400L;
    private static String DEFAULT_ANNOUNCE_MESSAGE = "Border shift soon! Grab your gear and move out!!";
    private static long MINIMUM_TICKS_TO_TRIGGER_EARLY_WARN = 600L;


    //// constructors & init

    /**
     * Action class to announce server-wide that a Shift is to take place a
     * set time before it is to occur.
     * 
     * @param jPlugin
     */
    public AnnounceBeforeShift(ShiftingPerspectives jPlugin)
    {
        this.doAnnounceBeforeShift = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE,
                                                                    false);
        if (doAnnounceBeforeShift)
        {
            this.plugin = jPlugin;
            init();
            plugin.logInfo("AnnounceBeforeShift enabled");
        }
    }

    /**
     * Initialise the required variables for use if Action enabled.
     * 
     * @param jPlugin
     */
    private void init()
    {
        this.announceBeforeShiftTime = plugin.getConfig().getLong(CFG_ANNOUNCE_TIME,
                                                                  DEFAULT_TICKS_BEFORE);
        this.announceBeforeShiftMessage = plugin.getConfig()
                .getString(CFG_ANNOUNCE_MESSAGE, DEFAULT_ANNOUNCE_MESSAGE);
    }

    //// gets & sets

    @Override
    public boolean isEnabled()
    {
        return doAnnounceBeforeShift;
    }

    @Override
    public boolean isActive()
    {
        return taskID != 0;
    }

    @Override
    public void setShiftTiming(long[] shiftTiming)
    {
        this.shiftTiming = shiftTiming;
    }


    //// public methods

    @Override
    public boolean start()
    {
        if (isEnabled())
        {
            plugin.logInfo("AnnounceBeforeShift.start()");
            RunAnnounceBeforeShift runAnnounce = new RunAnnounceBeforeShift();
            long ticksUntilNextShift = shiftTiming[0];
            if (ticksUntilNextShift - announceBeforeShiftTime <= MINIMUM_TICKS_TO_TRIGGER_EARLY_WARN)
            {
                // run announce immediately
                plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
                // queue task for following Shift
                long newNextRun = shiftTiming[0] + shiftTiming[1]
                        - announceBeforeShiftTime;
                taskID = plugin
                        .getServer()
                        .getScheduler()
                        .scheduleSyncRepeatingTask(plugin,
                                                   runAnnounce,
                                                   newNextRun,
                                                   shiftTiming[1]);
            }
            else
            {
                taskID = plugin
                        .getServer()
                        .getScheduler()
                        .scheduleSyncRepeatingTask(plugin,
                                                   runAnnounce,
                                                   shiftTiming[0]
                                                           - announceBeforeShiftTime,
                                                   shiftTiming[1]);
            }
            return true;
        }
        else
        {
            plugin.logInfo("AnnounceBeforeShift.start(), skipping - inactive");
            return false;
        }
    }

    @Override
    public boolean stop()
    {
        if (isEnabled() && isActive())
        {
            plugin.logInfo("AnnounceBeforeShift.stop()");
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = 0;
            return true;
        }
        else
        {
            plugin.logInfo("AnnounceBeforeShift.stop(), skipping - inactive");
            return false;
        }
    }

    @Override
    public boolean reset()
    {
        stop();
        start();
        return true;
    }

    //// announce task

    /**
     * Runnable class to perform the server-wide warning preceding an upcoming
     * world border Shift.
     * 
     * @author indiv
     * 
     */
    private class RunAnnounceBeforeShift
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.logInfo("RunAnnounceBeforeShift");
            plugin.getServer().broadcastMessage(announceBeforeShiftMessage);
        }
    }


}
