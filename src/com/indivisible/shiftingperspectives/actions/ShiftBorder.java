package com.indivisible.shiftingperspectives.actions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Action class to perform the world border modifications. The guts of the
 * plugin.
 * 
 * @author indiv
 * 
 */
public class ShiftBorder
        extends Action
{

    //// data

    private JavaPlugin plugin;
    private List<ShiftSubAction> announceActions;
    private int taskID = 0;
    private int lastDayRun;
    private long lastTickRun;

    private String shiftTimingMode;
    private int shiftTimingDays;
    private long shiftTimingTicks;

    private boolean doRelocate;
    private int relocateDistance;
    private String relocateDirection;   //ASK Better way to store the direction than String? Enum?

    private boolean doResize;
    private int resizeAmount;

    private static String CFG_TIMING_MODE = "settings.timing.mode";
    private static String CFG_TIMING_DAYS = "settings.timing.days";
    private static String CFG_TIMING_TICKS = "settings.timing.ticks";
    private static String CFG_RELOCATE_ACTIVE = "settings.border.relocate.active";
    private static String CFG_RELOCATE_DISTANCE = "settings.border.relocate.distance";
    private static String CFG_RELOCATE_DIRECTION = "settings.border.relocate.direction";
    private static String CFG_RESIZE_ACTIVE = "settings.border.resize.active";
    private static String CFG_RESIZE_AMOUNT = "settings.border.resize.amount";

    private static String SHIFT_MODE_DAILY = "daily";
    private static String SHIFT_MODE_DAYS = "days";
    private static String SHIFT_MODE_TICKS = "ticks";
    private static long TICKS_MIDNIGHT = 18000L;
    private static long TICKS_FULL_DAY = 24000L;
    private static String DEFAULT_RELOCATE_DIRECTION = "north";


    //// constructor & init

    /**
     * Action class to perform the world border modifications.
     * 
     * @param jPlugin
     */
    public ShiftBorder(JavaPlugin jPlugin)
    {
        this.doRelocate = jPlugin.getConfig().getBoolean(CFG_RELOCATE_ACTIVE, false);
        this.doResize = jPlugin.getConfig().getBoolean(CFG_RESIZE_ACTIVE, false);
        if (doRelocate || doResize)
        {
            this.plugin = jPlugin;
            this.announceActions = new ArrayList<ShiftSubAction>();

            initTiming();
            if (doRelocate)
            {
                initRelocate();
            }
            if (doResize)
            {
                initResize();
            }
        }
    }

    /**
     * Initialise needed data members for enabled border modification actions.
     */
    private void initTiming()
    {
        this.shiftTimingMode = plugin.getConfig().getString(CFG_TIMING_MODE, "");
        if (isModeDaily())
        {
            initModeDaily();
        }
        else if (isModeDays())
        {
            initModeDays();
        }
        else if (isModeTicks())
        {
            initModeTicks();
        }
        else
        {
            plugin.getServer()
                    .getLogger()
                    .warning("[Shifting Perspectives] Incorrect Shift 'mode' set. Disabling plugin");
            plugin.onDisable();
        }
    }

    /**
     * Retrieve and set the necessary values for a once daily Shift.
     */
    private void initModeDaily()
    {
        plugin.getServer().getLogger()
                .info("[Shifting Perspectives] Enabling daily Shifts");
    }

    /**
     * Retrieve and set the necessary values for a Shift every X days.
     */
    private void initModeDays()
    {
        this.shiftTimingDays = plugin.getConfig().getInt(CFG_TIMING_DAYS);
        if (shiftTimingDays == 0)
        {
            plugin.getServer()
                    .getLogger()
                    .warning("[Shifting Perspectives] Incorrect 'timing.days' setting. Disabling plugin");
            plugin.onDisable();
        }
        else
        {
            plugin.getServer()
                    .getLogger()
                    .info(String.format("[Shifting Perspectives] Enabled multi-day Shifts. Run every %d days",
                                        shiftTimingDays));
        }
    }

    /**
     * Retrieve and set the necessary values for a Shift every X ticks.
     */
    private void initModeTicks()
    {
        this.shiftTimingTicks = plugin.getConfig().getLong(CFG_TIMING_TICKS, 0L);
        if (shiftTimingTicks == 0L)
        {
            plugin.getServer()
                    .getLogger()
                    .warning("[Shifting Perspectives] Incorrect 'timing.ticks' setting. Disabling plugin");
            plugin.onDisable();
        }
        else
        {
            plugin.getServer()
                    .getLogger()
                    .info(String.format("[Shifting Perspectives] Enabling custom tick Shifts. Run every %d ticks",
                                        shiftTimingTicks));
        }
    }

    /**
     * Retrieve and set the necessary values for the world border move action.
     */
    private void initRelocate()
    {
        this.relocateDistance = plugin.getConfig().getInt(CFG_RELOCATE_DISTANCE);   // 0 if not found
        this.relocateDirection = plugin.getConfig().getString(CFG_RELOCATE_DIRECTION,
                                                              DEFAULT_RELOCATE_DIRECTION);
    }

    /**
     * Retrieve and set the necessary values for the world border resize
     * action.
     */
    private void initResize()
    {
        this.resizeAmount = plugin.getConfig().getInt(CFG_RESIZE_AMOUNT);           // 0 if not found
    }


    //// public methods

    public boolean isEnabled()
    {
        return doRelocate || doResize;
    }

    public boolean isActive()
    {
        return taskID != 0;
    }

    public boolean start()
    {
        if (isEnabled())
        {
            long[] triggerTimes = null;
            if (isModeDaily())
            {
                triggerTimes = calcModeDailyTiming();
            }
            else if (isModeDays())
            {
                triggerTimes = calcModeDaysTiming();
            }
            else if (isModeTicks())
            {
                triggerTimes = calcModeTicksTiming();
            }

            RunShift runShift = new RunShift();
            taskID = plugin
                    .getServer()
                    .getScheduler()
                    .scheduleSyncRepeatingTask(plugin,
                                               runShift,
                                               triggerTimes[0],
                                               triggerTimes[1]);

            for (ShiftSubAction action : announceActions)
            {
                action.setShiftTiming(triggerTimes);
                action.start();
            }

            return true;
        }
        return false;
    }

    public boolean stop()
    {
        if (isActive())
        {
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = 0;
            // loop through the announce sub-actions and cancel their tasks if appropriate.
            for (ShiftSubAction action : announceActions)
            {
                action.stop();
            }
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


    //// private methods

    /**
     * Convenience method to test if Shift is configured to occur once daily.
     * 
     * @return
     */
    private boolean isModeDaily()
    {
        return shiftTimingMode.equals(SHIFT_MODE_DAILY);
    }

    /**
     * Convenience method to test if Shift is configured to occur every X
     * days.
     * 
     * @return
     */
    private boolean isModeDays()
    {
        return shiftTimingMode.equals(SHIFT_MODE_DAYS);
    }

    /**
     * Convenience method to test if Shift is configured to occur every X
     * ticks.
     * 
     * @return
     */
    private boolean isModeTicks()
    {
        return shiftTimingMode.equals(SHIFT_MODE_TICKS);
    }

    /**
     * Calculate the ticks to enqueue the Daily Shift task based off current
     * game time.
     * 
     * @return
     */
    private long[] calcModeDailyTiming()
    {
        long[] triggerTimes = new long[2];
        triggerTimes[0] = ticksUntilNextMidnight();
        triggerTimes[1] = TICKS_FULL_DAY;
        return triggerTimes;
    }

    /**
     * Calculate the ticks to enqueue the Multi-Day Shift task based off
     * current game time and last time run.
     * 
     * @return
     */
    private long[] calcModeDaysTiming()
    {
        long[] triggerTimes = new long[2];
        long currentFullTime = plugin.getServer().getWorld("world").getFullTime();

        int baseDay;
        if (lastDayRun == 0)
        {
            int currentDay = (int) (currentFullTime / TICKS_FULL_DAY);
            baseDay = currentDay;
        }
        else
        {
            baseDay = lastDayRun;
        }
        long fullTickRunTime = ((baseDay + shiftTimingDays) * TICKS_FULL_DAY)
                + TICKS_MIDNIGHT;
        triggerTimes[0] = fullTickRunTime - currentFullTime;

        triggerTimes[1] = TICKS_FULL_DAY * shiftTimingDays;
        return triggerTimes;
    }

    /**
     * Calculate the ticks to enqueue the Tick based Shift task based off
     * current game time.
     * 
     * @return
     */
    private long[] calcModeTicksTiming()
    {
        long[] triggerTimes = new long[2];

        if (lastTickRun == 0L)
        {
            triggerTimes[0] = shiftTimingTicks;
        }
        else
        {
            long currentFullTime = plugin.getServer().getWorld("world").getFullTime();
            long newTriggerTime = lastTickRun;
            do
            {
                newTriggerTime += shiftTimingTicks;
            }
            while (newTriggerTime - currentFullTime <= 0);
            triggerTimes[0] = newTriggerTime;
        }
        triggerTimes[1] = shiftTimingTicks;
        return triggerTimes;
    }

    /**
     * Calculate how many ticks until the next Midnight occurs in-game.
     * 
     * @return
     */
    private long ticksUntilNextMidnight()
    {
        long currentDayTime = plugin.getServer().getWorld("world").getTime();
        if (currentDayTime < TICKS_MIDNIGHT)
        {
            return TICKS_MIDNIGHT - currentDayTime;
        }
        else
        {
            return TICKS_FULL_DAY - currentDayTime + TICKS_MIDNIGHT;
        }
    }


    //// actions

    /**
     * Trigger the configured world border modifications.
     */
    private void performShift()
    {
        if (doRelocate)
        {
            performShiftRelocate();
        }
        if (doResize)
        {
            performShiftResize();
        }
    }

    /**
     * Make the changes to the world border needed to move it's location as
     * set in the configuration.
     */
    private void performShiftRelocate()
    {
        plugin.getServer()
                .getLogger()
                .info(String.format("[Shifting Perspectives] Relocating border %d blocks %s",
                                    relocateDistance,
                                    relocateDirection));
        //TODO make and call class to transpose the world border
    }

    /**
     * Make the changes to the world border needed to resize it as set in the
     * configuration.
     */
    private void performShiftResize()
    {
        plugin.getServer()
                .getLogger()
                .info(String.format("[Shifting Perspectives] Resizing border by %d blocks",
                                    resizeAmount));
        //TODO make and call class to resize the world border
    }

    private void resetSpawnLocation()
    {
        //TODO calculate the new centre of the accessible world and set the spawn point there.
        //REM get the max y-value of the location.
    }


    //// task

    /**
     * Runnable class to trigger the Shift Action.
     * 
     * @author indiv
     * 
     */
    private class RunShift
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().getLogger().info("[Shifting Perspectives] RunShift.run()");
            performShift();
            resetSpawnLocation();
            // update lastRun members for future use by reset() (Not needed for Daily)
            if (isModeDays())
            {
                lastDayRun = (int) (plugin.getServer().getWorld("world").getFullTime() / TICKS_FULL_DAY);
            }
            else if (isModeTicks())
            {
                lastTickRun = plugin.getServer().getWorld("world").getFullTime();
            }
        }

    }

}
