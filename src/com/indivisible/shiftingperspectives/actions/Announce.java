package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Announce
{

    //// data

    private JavaPlugin plugin;
    private boolean doAnnounce;
    private String announceText;
    private int interval;
    //private int lastAnnounceTime;

    private static final String DEFAULT_ANNOUNCEMENT = "This server is running ShiftingPerspectives. Prepare to have your world view changed!";


    //// constructors & init

    public Announce(JavaPlugin jPlugin)
    {
        doAnnounce = jPlugin.getConfig().getBoolean("announce.display", false);
        if (doAnnounce)
        {
            plugin = jPlugin;
            init();
        }
    }

    private void init()
    {
        interval = plugin.getConfig().getInt("announce.interval");
        announceText = plugin.getConfig().getString("announce.", DEFAULT_ANNOUNCEMENT);
    }

    //// gets & sets

    public boolean isActive()
    {
        return doAnnounce;
    }

    public int getInterval()
    {
        return interval;
    }


    //// public methods

    public boolean triggerAction()
    {
        makeAnnouncement();
        return true;
    }

    //// private methods

    //ASK calculate interval here or make a sep scheduler class to handle all tick calcs?

    //// announce task

    private void makeAnnouncement()
    {
        RunAnnounce runAnnounce = new RunAnnounce();
        plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
    }

    private class RunAnnounce
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().broadcastMessage(announceText);
        }
    }
}
