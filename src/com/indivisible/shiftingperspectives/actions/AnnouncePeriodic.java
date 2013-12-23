package com.indivisible.shiftingperspectives.actions;

import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.ShiftingPerspectives;

/**
 * Class to announce server-wide in game that the plugin is installed.
 * 
 * @author indiv
 * 
 */
public class AnnouncePeriodic
        extends Action
{

    //// data

    private ShiftingPerspectives plugin = null;

    private boolean doAnnouncePeriodic;
    private String announcePeriodicMessage;
    private long announcePeriodicInterval = 0L;
    private int taskID = 0;

    private static final String CFG_ANNOUNCE_ACTIVE = "settings.announce.periodic.active";
    private static final String CFG_ANNOUNCE_INTERVAL = "settings.announce.periodic.interval";
    private static final String CFG_ANNOUNCE_MESSAGE = "settings.announce.periodic.message";

    private static final String DEFAULT_ANNOUNCE_MESSAGE = "This server is running ShiftingPerspectives. Prepare to have your world view changed...";


    //// constructors & init

    /**
     * Class to announce server-wide in game that the plugin is installed.
     * 
     * @param jPlugin
     */
    public AnnouncePeriodic(ShiftingPerspectives jPlugin)
    {
        doAnnouncePeriodic = jPlugin.getConfig().getBoolean(CFG_ANNOUNCE_ACTIVE, false);
        if (doAnnouncePeriodic)
        {
            plugin = jPlugin;
            init();
            plugin.logInfo("AnnouncePeriodic enabled");
        }
    }

    /**
     * Initialise needed data members if Action enabled in configuration.
     */
    private void init()
    {
        announcePeriodicInterval = plugin.getConfig().getInt(CFG_ANNOUNCE_INTERVAL);
        announcePeriodicMessage = plugin.getConfig().getString(CFG_ANNOUNCE_MESSAGE,
                                                               DEFAULT_ANNOUNCE_MESSAGE);
    }

    //// gets

    @Override
    public boolean isEnabled()
    {
        return doAnnouncePeriodic;
    }

    @Override
    public boolean isActive()
    {
        return taskID != 0;
    }


    //// public methods

    @Override
    public boolean start()
    {
        plugin.logInfo("AnnouncePeriodic.start()");
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
        else
        {
            plugin.logInfo("AnnouncePeriodic.start(), skipping - inactive");
            return false;
        }
    }

    @Override
    public boolean stop()
    {
        plugin.logInfo("AnnouncePeriodic.stop()");
        if (isActive())
        {
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = 0;
            return true;
        }
        else
        {
            plugin.logInfo("AnnouncePeriodic.stop(), skipping - inactive");
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
     * Runnable class to perform the server-wide announcement.
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
            plugin.getServer().broadcastMessage(announcePeriodicMessage);
        }
    }

}
