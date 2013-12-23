package com.indivisible.shiftingperspectives;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.indivisible.shiftingperspectives.actions.Action;
import com.indivisible.shiftingperspectives.actions.AnnouncePeriodic;
import com.indivisible.shiftingperspectives.actions.ShiftBorder;

/**
 * Main class. Monitors world time and sets member Actions to run.
 * 
 * @author indiv
 */
public final class ShiftingPerspectives
        extends JavaPlugin
{

    //// data

    protected int taskID = 0;
    private List<Action> actions;

    private static final String LOG_STRING = "[>>>>>>] %s";
    private static final long UPDATE_FREQ = 800L;
    private static final long TOLLERENCE = 100L;


    //// plugin methods

    @Override
    public void onEnable()
    {
        this.logInfo("onEnable()");
        this.saveDefaultConfig();
        //ASK ^ reloads configs from file?? doesn't seem to

        actions = new ArrayList<Action>();
        actions.add(new AnnouncePeriodic(this));
        actions.add(new ShiftBorder(this));

        startTasks();
    }

    @Override
    public void onDisable()
    {
        this.logInfo("onDisable()");
        stopTasks();
        actions = null;
    }

    /**
     * Check if plugin is actively running and has a task in scheduler.
     * 
     * @return
     */
    private boolean isActive()
    {
        return taskID != 0;
    }


    //// enable and disable tasks

    /**
     * Starts up monitor and child Action tasks.
     */
    private void startTasks()
    {
        this.logInfo("allActions.start()");

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

    /**
     * Stops all running tasks.
     */
    private void stopTasks()
    {
        this.logInfo("allActions.stop()");
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

    /**
     * Rests all tasks. Called when game time changes out of sync with the
     * TimeMonitor.
     */
    private void resetTasks()
    {
        this.logInfo("allActions.reset()");
        for (Action action : actions)
        {
            action.reset();
        }
    }


    //// logging and messaging

    /**
     * Write a message to the server log of INFO level
     * 
     * @param msg
     */
    public void logInfo(String msg)
    {
        this.getServer().getLogger().info(String.format(LOG_STRING, msg));
    }

    /**
     * Write a message to the server log of WARNING level
     * 
     * @param msg
     */
    public void logWarn(String msg)
    {
        this.getServer().getLogger().warning(String.format(LOG_STRING, msg));
    }

    public void sayToServer(String msg)
    {
        this.getServer().broadcastMessage(msg);
    }

    public void sayToPlayer(Player player, String msg)
    {
        //TODO method to send message to one player
    }


    //// Time watching task

    /**
     * Runnable class to monitor game time for changes and synchronisation.
     * discrepancies
     * 
     * @author indiv
     */
    private class TimeMonitor
            extends BukkitRunnable
    {

        private ShiftingPerspectives plugin;
        private long expectedTime = 0;

        public TimeMonitor(ShiftingPerspectives jPlugin)
        {
            this.plugin = jPlugin;
            expectedTime = jPlugin.getServer().getWorld("world").getFullTime()
                    + UPDATE_FREQ;
        }

        @Override
        public void run()
        {
            long currentFullTime = plugin.getServer().getWorld("world").getFullTime();
            if (currentFullTime - expectedTime > TOLLERENCE)
            {
                plugin.logInfo("TimeMonitor: Caught time discrepency. Resetting Action Tasks");
                resetTasks();
                expectedTime = currentFullTime + UPDATE_FREQ;
            }
            expectedTime += UPDATE_FREQ;

        }
    }

}
