package org.fracturedsmp.finality;

import org.bukkit.plugin.java.JavaPlugin;
import org.fracturedsmp.finality.commands.PlotArmorCommand;
import org.fracturedsmp.finality.data.PlayerData;
import org.fracturedsmp.finality.data.YamlHandler;
import org.fracturedsmp.finality.listeners.DeathKickListener;
import org.fracturedsmp.finality.listeners.PlayerDataListener;
import org.fracturedsmp.finality.listeners.PlotArmorListener;

import java.util.Map;

public final class Finality extends JavaPlugin {
    public static Finality INSTANCE;
    public static Map<String, PlayerData> PLAYERS;
    public static YamlHandler YAML;
    private boolean enabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        YAML = new YamlHandler(this);
        PLAYERS = YAML.loadPlayerData();

        this.getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                () -> YAML.savePlayerData(PLAYERS),
                6000L,
                6000L
        ); //TODO: make configurable


        // Register listeners
        this.getServer().getPluginManager().registerEvents(new DeathKickListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDataListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlotArmorListener(), this);

        //register commands
        getCommand("plotarmor").setExecutor(new PlotArmorCommand());

        enabled = true;
    }

    @Override
    public void onDisable() {
        if(!enabled) return;
        // Plugin shutdown logic
        YAML.savePlayerData(PLAYERS);
    }
}
