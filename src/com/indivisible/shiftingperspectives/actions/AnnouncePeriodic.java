package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class AnnouncePeriodic
        extends Action
{

    //// data

    private JavaPlugin plugin = null;

    private boolean doAnnouncePeriodic;
    private String announcePeriodicMessage;
    private long announcePeriodicInterval = 0L;
    private int taskID = 0;

    private static final String CFG_ANNOUNCE_ACTIVE = "settings.announce.periodic.active";
    private static final String CFG_ANNOUNCE_INTERVAL = "settings.announce.periodic.interval";
    private static final String CFG_ANNOUNCE_MESSAGE = "settings.announce.periodic.message";

    private static final String DEFAULT_ANNOUNCE_MESSAGE = "This server is running ShiftingPerspectives. Prepare to have your world view changed...";


    //// constructors & init

    public AnnouncePeriodic(JavaPlugin jPlugin)
    {
        doAnnouncePeriodic = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE, false);
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

    //// gets

    public boolean isEnabled()
    {
        return doAnnouncePeriodic;
    }

    public boolean isActive()
    {
        return taskID != 0;
    }


    //// public methods

    public boolean start()
    {
        plugin.getServer().getLogger()
                .info("[ShiftingPerspectives] AnnouncePeriodic.start()");
        if (doAnnouncePeriodic && announcePeriodicInterval != 0L)
        {
            RunAnnounce runAnnounce = new RunAnnounce();
            taskID = plugin
                    .getServer()
                    .getScheduler()
                    .scheduleSyncRepeatingTask(plugin,
                                               runAnnounce,
                                               100L,
                                               announcePeriodicInterval);
            return true;
        }
        return false;
    }

    public boolean stop()
    {
        plugin.getServer().getLogger()
                .info("[ShiftingPerspectives] AnnouncePeriodic.stop()");
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
            plugin.getServer().broadcastMessage(announcePeriodicMessage);
        }
    }

}
