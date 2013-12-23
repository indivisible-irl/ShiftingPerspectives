package com.indivisible.shiftingperspectives;

//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.actions.Action;
import com.indivisible.shiftingperspectives.actions.AnnouncePeriodic;
import com.indivisible.shiftingperspectives.actions.ShiftBorder;

public final class ShiftingPerspectives
        extends JavaPlugin
{

    //// data

    protected int taskID = 0;
    private List<Action> actions;

    private static final long UPDATE_FREQ = 1000L;
    private static final long TOLLERENCE = 200L;


    //// plugin methods

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        actions = new ArrayList<Action>();
        actions.add(new AnnouncePeriodic(this));
        actions.add(new ShiftBorder(this));

        startTasks();
    }

    @Override
    public void onDisable()
    {
        this.getServer().getLogger().info("=== onDisable()");
        stopTasks();
        actions = null;
    }


    //// enable and disable tasks

    private void startTasks()
    {
        this.getServer().getLogger().info("[ShiftingPerspectives] allActions.start()");

        TimeMonitor timeMonitor = new TimeMonitor(this);
        taskID = this
                .getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(this,
                                           timeMonitor,
                                           UPDATE_FREQ / 2,
                                           UPDATE_FREQ);
        for (Action action : actions)
        {
            action.start();
        }
    }

    private void stopTasks()
    {
        this.getServer().getLogger().info("[ShiftingPerspectives] allActions.stop()");
        if (isActive())
        {
            this.getServer().getScheduler().cancelTask(taskID);
            taskID = 0;
        }
        for (Action action : actions)
        {
            action.stop();
        }
    }

    private void resetTasks()
    {
        this.getServer().getLogger().info("[ShiftingPerspectives] allActions.reset()");
        for (Action action : actions)
        {
            action.reset();
        }
    }


    private boolean isActive()
    {
        return taskID != 0;
    }


    //// Time watching task

    private class TimeMonitor
            extends BukkitRunnable
    {

        private JavaPlugin plugin;
        private long lastCheckedTime = 0;

        public TimeMonitor(JavaPlugin jPlugin)
        {
            this.plugin = jPlugin;
            lastCheckedTime = jPlugin.getServer().getWorld("world").getFullTime();
        }

        @Override
        public void run()
        {
            long currentFullTime = plugin.getServer().getWorld("world").getFullTime();
            if (currentFullTime - lastCheckedTime > TOLLERENCE)
            {
                resetTasks();
            }
            lastCheckedTime = currentFullTime;

        }
    }

}
