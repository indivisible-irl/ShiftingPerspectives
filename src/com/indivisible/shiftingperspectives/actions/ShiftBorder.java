package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;


public class ShiftBorder
        extends Action
{

    //// data

    private JavaPlugin plugin;

    private boolean doRelocate;
    private int relocateDistance;
    private String relocateDirection;   //ASK Better way to store the direction?

    private boolean doResize;
    private int resizeAmount;

    private static String CFG_RELOCATE_ACTIVE = "settings.border.relocate.active";
    private static String CFG_RELOCATE_DISTANCE = "settings.border.relocate.distance";
    private static String CFG_RELOCATE_DIRECTION = "settings.border.relocate.direction";
    private static String CFG_RESIZE_ACTIVE = "settings.border.resize.active";
    private static String CFG_RESIZE_AMOUNT = "settings.border.resize.amount";

    private static String DEFAULT_RELOCATE_DIRECTION = "north";


    //// constructor & init
    public ShiftBorder(JavaPlugin jPlugin)
    {
        this.doRelocate = jPlugin.getConfig().getBoolean(CFG_RELOCATE_ACTIVE, false);
        this.doResize = jPlugin.getConfig().getBoolean(CFG_RESIZE_ACTIVE, false);
        if (doRelocate || doResize)
        {
            this.plugin = jPlugin;
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


    @Override
    public boolean isActive()
    {
        return doRelocate || doResize;
    }

    @Override
    public boolean triggerAction(long worldTicksTotal)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean checkShouldRun(long worldTicksTotal)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
