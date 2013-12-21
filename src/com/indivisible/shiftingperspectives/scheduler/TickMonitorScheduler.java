package com.indivisible.shiftingperspectives.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.actions.Announcer;


public class TickMonitorScheduler
{

    private JavaPlugin plugin;
    private Announcer announcer;

    private static final long UPDATE_FREQ = 600L;

    public TickMonitorScheduler(JavaPlugin jPlugin, Announcer anouncer)
    {
        this.plugin = jPlugin;
        this.announcer = announcer;
        //TODO add new timed actions as and when written
    }


    private void queueUpdate()
    {
        Update update = new Update();
        plugin.getServer().getScheduler().runTaskLater(plugin, update, UPDATE_FREQ);
    }


    private class Update
            extends BukkitRunnable
    {

        private boolean announce;
        private boolean shift;

        //        public Update(boolean runAnnouncer)
        //        {
        //            announce = runAnnouncer;
        //        }

        @Override
        public void run()
        {
            //            if (announce)
            //            {
            //                announcer.
            //            }
        }

    }
}
