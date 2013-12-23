package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class ShiftBorder
        extends Action
{

    //// data

    private JavaPlugin plugin;
    private AnnounceBeforeShift announceBeforeShift;
    private AnnounceAtRisk announceAtRisk;

    private String shiftTimingMode;
    private int shiftTimingDays;
    private long shiftTimingTicks;
    private long lastRunTicks;
    private int lastRunDay;

    private boolean doRelocate;
    private int relocateDistance;
    private String relocateDirection;   //ASK Better way to store the direction?

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

    private static String DEFAULT_RELOCATE_DIRECTION = "north";
    private static long TICKS_MIDNIGHT = 18000L;
    private static long TICKS_FULL_DAY = 24000L;
    private static String SHIFT_MODE_DAILY = "daily";
    private static String SHIFT_MODE_DAYS = "days";
    private static String SHIFT_MODE_TICKS = "ticks";


    //// constructor & init
    public ShiftBorder(JavaPlugin jPlugin)
    {
        this.doRelocate = jPlugin.getConfig().getBoolean(CFG_RELOCATE_ACTIVE, false);
        this.doResize = jPlugin.getConfig().getBoolean(CFG_RESIZE_ACTIVE, false);
        if (doRelocate || doResize)
        {
            this.plugin = jPlugin;
            this.announceBeforeShift = new AnnounceBeforeShift(jPlugin);
            this.announceAtRisk = new AnnounceAtRisk(jPlugin);

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

    private void initTiming()
    {
        this.shiftTimingMode = plugin.getConfig().getString(CFG_TIMING_MODE, "");
        if (shiftTimingMode.equals(SHIFT_MODE_DAILY))
        {

        }
        else if (shiftTimingMode.equals(SHIFT_MODE_DAYS))
        {
            this.shiftTimingDays = plugin.getConfig().getInt(CFG_TIMING_DAYS);
            if (shiftTimingDays == 0)
            {
                plugin.getServer().getLogger()
                        .warning("=== Incorrect Timing 'days' setting. Disabling plugin");
                plugin.onDisable();
            }
        }
        else if (shiftTimingMode.equals(SHIFT_MODE_TICKS))
        {
            this.shiftTimingTicks = plugin.getConfig().getLong(CFG_TIMING_TICKS);
            if (shiftTimingTicks == 0L)
            {
                plugin.getServer()
                        .getLogger()
                        .warning("=== Incorrect Timing 'ticks' setting. Disabling plugin");
                plugin.onDisable();
            }
        }
        else
        {
            plugin.getServer().getLogger()
                    .warning("=== Incorrect Shift 'mode'. Disabling plugin");
            plugin.onDisable();
            //ASK set mod_active: false?
        }
    }

    private void initRelocate()
    {
        this.relocateDistance = plugin.getConfig().getInt(CFG_RELOCATE_DISTANCE);   // 0 if not found
        this.relocateDirection = plugin.getConfig().getString(CFG_RELOCATE_DIRECTION,
                                                              DEFAULT_RELOCATE_DIRECTION);
    }

    private void initResize()
    {
        this.resizeAmount = plugin.getConfig().getInt(CFG_RESIZE_AMOUNT);           // 0 if not found
    }


    //// action methods

    @Override
    public boolean isActive()
    {
        return doRelocate || doResize;
    }

    @Override
    public boolean triggerAction(long worldTicksTotal)
    {
        if (isActive())
        {
            plugin.getServer().getLogger().info("=== shift.triggerAction()");
            if (checkShouldRun(worldTicksTotal))
            {
                lastRunTicks = worldTicksTotal;
                lastRunDay = (int) (worldTicksTotal / TICKS_FULL_DAY);

                RunShift runShift = new RunShift();
                plugin.getServer().getScheduler().runTask(plugin, runShift);

                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean checkShouldRun(long worldTicksTotal)
    {
        if (shiftTimingMode.equals(SHIFT_MODE_DAILY))
        {
            long timeOfDay = plugin.getServer().getWorld("world").getTime();
            if (worldTicksTotal - lastRunTicks > TICKS_MIDNIGHT
                    && timeOfDay - TICKS_MIDNIGHT > 0)
            {
                return true;
            }
        }
        else if (shiftTimingMode.equals(SHIFT_MODE_DAYS))
        {
            int today = (int) (worldTicksTotal / TICKS_FULL_DAY);
            if (lastRunDay == 0L)
            {
                lastRunDay = today;
            }
            else
            {
                long timeOfDay = plugin.getServer().getWorld("world").getTime();
                if (today - lastRunDay >= 0 && worldTicksTotal - lastRunTicks > 18000
                        && timeOfDay - TICKS_MIDNIGHT > 0)
                {
                    return true;
                }
            }
        }
        else if (shiftTimingMode.equals(SHIFT_MODE_TICKS))
        {
            if (lastRunTicks == 0L)
            {
                lastRunTicks = worldTicksTotal;
            }
            else if (worldTicksTotal - lastRunTicks > shiftTimingTicks)
            {
                return true;
            }
        }
        return false;
    }

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

    private void performShiftRelocate()
    {
        plugin.getServer()
                .getLogger()
                .info(String.format("=== Relocating border %d blocks %s",
                                    relocateDistance,
                                    relocateDirection));
    }

    private void performShiftResize()
    {
        plugin.getServer().getLogger()
                .info(String.format("=== Resizing border by %d blocks", resizeAmount));
    }


    private class RunShift
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().getLogger().info("=== RunShift.run()");
            performShift();
        }

    }

}
