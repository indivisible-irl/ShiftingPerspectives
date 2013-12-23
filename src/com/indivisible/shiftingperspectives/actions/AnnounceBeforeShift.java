package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    private JavaPlugin plugin;
    private int taskID = 0;
    private long[] shiftTiming = null;

    private boolean doAnnounceBeforeShift;
    private long announceBeforeShiftTime;
    private String announceBeforeShiftMessage;

    private static String CFG_ANNOUNCE_ACTIVE = "settings.announce.warn_before_shift.active";
    private static String CFG_ANNOUNCE_TIME = "settings.announce.warn_before_shift.ticks_before";
    private static String CFG_ANNOUNCE_MESSAGE = "settings.announce.warn_before_shift.message";

    private static long DEFAULT_TIME_BEFORE = 2400L;
    private static String DEFAULT_ANNOUNCE_MESSAGE = "Border shift soon. Grab your gear and move out!!";
    private static long MINIMUM_TIME_TO_TRIGGER_EARLY_WARN = 600L;


    //// constructors & init

    /**
     * Action class to announce server-wide that a Shift is to take place a
     * set time before it is to occur.
     * 
     * @param jPlugin
     */
    public AnnounceBeforeShift(JavaPlugin jPlugin)
    {
        this.doAnnounceBeforeShift = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE,
                                                                    false);
        if (doAnnounceBeforeShift)
        {
            init(jPlugin);
        }
    }

    /**
     * Initialise the required variables for use if Action enabled.
     * 
     * @param jPlugin
     */
    private void init(JavaPlugin jPlugin)
    {
        this.plugin = jPlugin;
        this.announceBeforeShiftTime = plugin.getConfig().getLong(CFG_ANNOUNCE_TIME,
                                                                  DEFAULT_TIME_BEFORE);
        this.announceBeforeShiftMessage = plugin.getConfig()
                .getString(CFG_ANNOUNCE_MESSAGE, DEFAULT_ANNOUNCE_MESSAGE);
    }


    //// gets & sets

    public boolean isEnabled()
    {
        return doAnnounceBeforeShift;
    }

    public boolean isActive()
    {
        return taskID != 0;
    }

    public void setShiftTiming(long[] shiftTiming)
    {
        this.shiftTiming = shiftTiming;
    }


    //// public methods

    public boolean start()
    {
        if (isEnabled())
        {
            RunAnnounce runAnnounce = new RunAnnounce();
            long ticksUntilNextShift = shiftTiming[0];
            if (ticksUntilNextShift - announceBeforeShiftTime <= MINIMUM_TIME_TO_TRIGGER_EARLY_WARN)
            {
                plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
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
        }
        return false;
    }

    @Override
    public boolean stop()
    {
        if (isActive())
        {
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = 0;
            return true;
        }
        return false;
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
    private class RunAnnounce
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().broadcastMessage(announceBeforeShiftMessage);
        }
    }


}
