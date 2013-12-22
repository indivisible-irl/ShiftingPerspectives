package com.indivisible.shiftingperspectives;

//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.indivisible.shiftingperspectives.actions.Announce;

public final class ShiftingPerspectives
        extends JavaPlugin
{

    //// data

    private Announce announcer;
    protected int nextTaskID = Integer.MIN_VALUE;
    private static final long UPDATE_FREQ = 600L;


    //// plugin methods

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        this.getServer().getLogger().info("=== onEnable()");
        this.announcer = new Announce(this);

        startUpdating();
        testConfig();
    }

    @Override
    public void onDisable()
    {
        this.getServer().getLogger().info("=== onDisable()");
        stopUpdating();
        this.announcer = null;
    }


    //// enable and disable the scheduling

    private void startUpdating()
    {
        this.getServer().getLogger().info("=== startUpdating()");
        queueNextUpdateTask();
    }

    private void stopUpdating()
    {
        this.getServer().getLogger().info("=== stopUpdating()");
        if (nextTaskID != Integer.MIN_VALUE)
        {
            this.getServer().getScheduler().cancelTask(nextTaskID);
        }
    }

    private void testConfig()
    {
        this.getServer().getLogger().info("=== CONFIG TESTING ");
        String test_str = this.getConfig().getString("settings.timing.mode",
                                                     "DEFAULT VALUE");
        this.getServer().getLogger().info("settings.timing.mode: " + test_str);
        test_str = this.getConfig().getString("settings.announce.msg_periodic",
                                              "DEFAULT VALUE");
        this.getServer().getLogger().info("settings.announce.msg_periodic: " + test_str);
    }


    //// update tasks

    private void queueNextUpdateTask()
    {
        this.getServer().getLogger().info("=== queueNextUpdateTask()");
        Update update = new Update();
        BukkitTask nextTask = this.getServer().getScheduler()
                .runTaskLater(this, update, UPDATE_FREQ);
        nextTaskID = nextTask.getTaskId();
    }

    private void performUpdate()
    {
        this.getServer().getLogger().info("=== performUpdate()");
        World world = this.getServer().getWorld("world");
        if (world != null)
        {
            long now = this.getServer().getWorld("world").getFullTime();
            announcer.triggerAction(now);
        }
        else
        {
            this.getServer().getLogger().warning("=== Unable to access world");
        }

        queueNextUpdateTask();
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

    //    @Override
    //    public boolean onCommand(CommandSender sender,
    //                             Command command,
    //                             String label,
    //                             String[] args)
    //    {
    //        // command root to be '/shift <args>'
    //
    //        //ASK false if not catching command?
    //        return false;
    //    }


}
