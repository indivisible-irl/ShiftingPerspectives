package com.indivisible.shiftingperspectives.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.actions.Announce;


public class TickMonitorScheduler
{

    //// data

    private JavaPlugin plugin;
    private Announce announcer;
    private int nextTaskID = Integer.MIN_VALUE;

    private static final long UPDATE_FREQ = 600L;


    //// constructors & init

    public TickMonitorScheduler(JavaPlugin jPlugin, Announce announcer)
    {
        this.plugin = jPlugin;

        //REM add new timed actions as and when written
        this.announcer = announcer;
    }


    //// public methods

    public void startUpdating()
    {
        //ASK run everything once then queue as normal or just queue from now?

    }

    public void stopUpdating()
    {
        if (nextTaskID != Integer.MIN_VALUE)
        {
            plugin.getServer().getScheduler().cancelTask(nextTaskID);
        }
    }


    //// private methods

    private void performUpdate()
    {
        //TODO loop through actions and trigger their triggers
        long now = plugin.getServer().getWorld("world").getFullTime();

    }

    private void queueUpdate()
    {
        Update update = new Update();
        plugin.getServer().getScheduler().runTaskLater(plugin, update, UPDATE_FREQ);
    }


    //// runnable

    private class Update
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            performUpdate();
        }

    }
}
