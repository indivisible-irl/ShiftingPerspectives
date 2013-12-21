package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Announce
        extends Action
{

    //// data

    private JavaPlugin plugin;
    private boolean doAnnounce;
    private String announceText;
    private long interval;
    private long lastAnnounceTime;

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

    public long getInterval()
    {
        return interval;
    }


    //// public methods


    public boolean triggerAction(long worldTicksTotal)
    {
        if (checkShouldRun(worldTicksTotal))
        {
            lastAnnounceTime = worldTicksTotal;
            makeAnnouncement();
        }
        return true;
    }


    //// private methods

    protected boolean checkShouldRun(long worldTicksTotal)
    {
        if (lastAnnounceTime == 0L) // default value if long set
        {
            return true;
        }
        else
        {
            if (worldTicksTotal - lastAnnounceTime > interval)
            {
                return true;
            }
        }
        return false;
    }


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
