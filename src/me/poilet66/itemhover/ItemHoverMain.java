package me.poilet66.itemhover;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemHoverMain extends JavaPlugin {

    @Override
    public void onEnable() {
        if(!setupDepend()) {
            getLogger().severe("[" + getDescription().getName() + "] - Disabled due to no TownyChat dependency found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {

    }

    private boolean setupDepend() {
        if(getServer().getPluginManager().getPlugin("TownyChat") == null) {
            return false;
        }
        return true;
    }

}
