package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class WarnAtRiskUsers
        extends ShiftSubAction
{

    //// data

    private JavaPlugin plugin;
    private long[] shiftTiming = null;

    private boolean doAnnounceAtRisk;
    private long warnBeforeTicks;
    private String warnMessage;
    private int taskID = 0;

    private static String CFG_WARN_ACTIVE = "settings.announce.warn_at_risk_users.active";
    private static String CFG_WARN_BEFORE_TICKS = "settings.warn_at_risk_users.ticks_before";
    private static String CFG_WARN_MESSAGE = "settings.warn_at_risk_users.message";
    private static String DEFAULT_WARN_MESSAGE = "You are about to be Shifted, prepare yourself and move %s!";
    private static long MINIMUM_TIME_TO_TRIGGER_EARLY_WARN = 600L;


    //// constructors & init

    public WarnAtRiskUsers(JavaPlugin jPlugin, ShiftBorder shiftBorder)
    {
        doAnnounceAtRisk = jPlugin.getConfig().getBoolean(CFG_WARN_ACTIVE, true);
        if (doAnnounceAtRisk)
        {
            this.plugin = jPlugin;
            init();
        }
    }

    private void init()
    {
        this.warnBeforeTicks = plugin.getConfig().getLong(CFG_WARN_BEFORE_TICKS);
        this.warnMessage = plugin.getConfig().getString(CFG_WARN_MESSAGE,
                                                        DEFAULT_WARN_MESSAGE);
    }


    //// gets & sets

    public boolean isEnabled()
    {
        return doAnnounceAtRisk;
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
            if (ticksUntilNextShift - warnBeforeTicks <= MINIMUM_TIME_TO_TRIGGER_EARLY_WARN)
            {
                plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
                long newNextRun = shiftTiming[0] + shiftTiming[1] - warnBeforeTicks;
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
                                                   shiftTiming[0] - warnBeforeTicks,
                                                   shiftTiming[1]);
            }
        }
        return false;
    }

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

    public boolean reset()
    {
        stop();
        start();
        return true;
    }


    //// announce task

    private class RunAnnounce
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            //TODO search for users in areas about to get removed and 'pm' them the message.
            //TODO method to get reverse of movement direction for substitution in message
            plugin.getServer().broadcastMessage(String.format(warnMessage, "south"));
        }
    }

}
