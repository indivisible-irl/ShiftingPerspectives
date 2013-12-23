package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class AnnouncePeriodic
        extends Action
{

    //// data

    private JavaPlugin plugin;

    private boolean doAnnouncePeriodic;
    private String announcePeriodicMessage;
    private long announcePeriodicInterval;
    private long lastPeriodicAnnounceTicks;

    //    private boolean doWarnAtRiskUsers;
    //    private long warnAtRiskUsersTimeBeforeShift;
    //    private String warnAtRiskUsersMessage;

    private static final String CFG_ANNOUNCE_ACTIVE = "settings.announce.periodic.active";
    private static final String CFG_ANNOUNCE_INTERVAL = "settings.announce.periodic.interval";
    private static final String CFG_ANNOUNCE_MESSAGE = "settings.announce.periodic.message";

    private static final String DEFAULT_ANNOUNCE_MESSAGE = "This server is running ShiftingPerspectives. Prepare to have your world view changed!";


    //// constructors & init

    public AnnouncePeriodic(JavaPlugin jPlugin)
    {
        doAnnouncePeriodic = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE, false);
        jPlugin.getServer().getLogger()
                .info("=== Periodic Announce: " + doAnnouncePeriodic);
        if (doAnnouncePeriodic)
        {
            plugin = jPlugin;
            init();
        }
    }

    private void init()
    {
        announcePeriodicInterval = plugin.getConfig().getInt(CFG_ANNOUNCE_INTERVAL);
        announcePeriodicMessage = plugin.getConfig().getString(CFG_ANNOUNCE_MESSAGE,
                                                               DEFAULT_ANNOUNCE_MESSAGE);
    }

    //// gets & sets

    public boolean isActive()
    {
        return doAnnouncePeriodic;
    }

    public long getInterval()
    {
        return announcePeriodicInterval;
    }


    //// public methods


    public boolean triggerAction(long worldTicksTotal)
    {
        if (doAnnouncePeriodic)
        {
            plugin.getServer().getLogger().info("=== triggerAction() | enabled");
            if (checkShouldRun(worldTicksTotal))
            {
                lastPeriodicAnnounceTicks = worldTicksTotal;
                makeAnnouncement();
            }
            return true;
        }
        else
        {
            plugin.getServer().getLogger().info("=== triggerAction() | not enabled");
            return false;
        }

    }


    //// private methods

    protected boolean checkShouldRun(long worldTicksTotal)
    {
        if (lastPeriodicAnnounceTicks == 0L) // default value if long not set
        {
            return true;
        }
        else if (worldTicksTotal - lastPeriodicAnnounceTicks > announcePeriodicInterval)
        {
            return true;
        }
        return false;
    }


    //// announce task

    private void makeAnnouncement()
    {
        plugin.getServer().getLogger().info("=== makeAnnouncement()");
        RunAnnounce runAnnounce = new RunAnnounce();
        plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
    }

    private class RunAnnounce
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().broadcastMessage(announcePeriodicMessage);
        }
    }

}
