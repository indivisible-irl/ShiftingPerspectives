package com.indivisible.shiftingperspectives;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.actions.Announce;

public final class ShiftingPerspectives
        extends JavaPlugin
{

    //// data

    private Announce announcer;
    private int nextTaskID = Integer.MIN_VALUE;
    private static final long UPDATE_FREQ = 600L;


    //// plugin methods

    @Override
    public void onEnable()
    {
        this.announcer = new Announce(this);

        startUpdating();
    }

    @Override
    public void onDisable()
    {
        stopUpdating();
        this.announcer = null;
    }


    //// enable and disable the scheduling

    private void startUpdating()
    {
        //ASK run everything once then queue as normal or just queue from now?

    }

    private void stopUpdating()
    {
        if (nextTaskID != Integer.MIN_VALUE)
        {
            this.getServer().getScheduler().cancelTask(nextTaskID);
        }
    }

    private void performUpdate()
    {
        //TODO loop through actions and trigger their triggers
        long now = this.getServer().getWorld("world").getFullTime();

    }

    private void queueUpdate()
    {
        Update update = new Update();
        this.getServer().getScheduler().runTaskLater(this, update, UPDATE_FREQ);
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

    //// command handling

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args)
    {
        // command root to be '/shift <args>'

        //ASK false if not catching command?
        return false;
    }


}
