package com.indivisible.shiftingperspectives.actions;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class AnnounceAtRisk
        extends Action
{

    //// data

    private JavaPlugin plugin;

    private boolean doAnnounceAtRisk;
    private long warnBeforeTicks;
    private String warnMessage;

    private static String CFG_WARN_ACTIVE = "settings.announce.warn_at_risk_users.active";
    private static String CFG_WARN_BEFORE_TICKS = "settings.warn_at_risk_users.ticks_before";
    private static String CFG_WARN_MESSAGE = "settings.warn_at_risk_users.message";
    private static String DEFAULT_WARN_MESSAGE = "You are about to be Shifted, prepare yourself!";


    //// constructors & init

    public AnnounceAtRisk(JavaPlugin jPlugin)
    {
        doAnnounceAtRisk = jPlugin.getConfig().getBoolean(CFG_WARN_ACTIVE);
        if (doAnnounceAtRisk)
        {
            this.plugin = jPlugin;
            init();
        }
    }

    private void init()
    {
        this.warnBeforeTicks = plugin.getConfig().getLong(CFG_WARN_BEFORE_TICKS);
        this.warnMessage = plugin.getConfig().getString(CFG_WARN_MESSAGE,
                                                        DEFAULT_WARN_MESSAGE);
    }


    //// public methods

    @Override
    public boolean isActive()
    {
        return doAnnounceAtRisk;
    }

    public long getAdvanceNoticeTicks()
    {
        return warnBeforeTicks;
    }

    @Override
    public boolean triggerAction(long worldTicksTotal)
    {
        if (isActive() && checkShouldRun(worldTicksTotal))
        {
            makeAnnouncement();
            return true;
        }
        return false;
    }


    //// private methods

    @Override
    protected boolean checkShouldRun(long worldTicksTotal)
    {
        // TODO Auto-generated method stub
        return false;
    }


    //// announce task

    private void makeAnnouncement()
    {
        plugin.getServer().getLogger().info("=== makeAnnouncement()");
        RunAnnounce runAnnounce = new RunAnnounce();
        plugin.getServer().getScheduler().runTask(plugin, runAnnounce);
    }

    private class RunAnnounce
            extends BukkitRunnable
    {

        @Override
        public void run()
        {
            plugin.getServer().broadcastMessage(warnMessage);
        }
    }

}
