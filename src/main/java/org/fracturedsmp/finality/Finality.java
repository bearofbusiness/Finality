package org.fracturedsmp.finality;

import org.bukkit.plugin.java.JavaPlugin;
import org.fracturedsmp.finality.commands.*;
import org.fracturedsmp.finality.data.PlayerData;
import org.fracturedsmp.finality.data.YamlHandler;
import org.fracturedsmp.finality.listeners.*;
import org.fracturedsmp.finality.util.NameGenerator;
import org.fracturedsmp.finality.util.SkinPool;

import java.util.Map;
import java.util.Objects;

public final class Finality extends JavaPlugin {
    public static Finality INSTANCE;
    public static Map<String, PlayerData> PLAYERS;
    public static YamlHandler YAML;
    public SkinPool skinPool;
    private boolean enabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        YAML = new YamlHandler(this);
        PLAYERS = YAML.loadPlayerData();
        skinPool = new SkinPool(this);

        NameGenerator.initWordlists(getClassLoader());

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
        this.getServer().getPluginManager().registerEvents(new CommandNickRewriteListener(), this);

        //register commands
        Objects.requireNonNull(getCommand("plotarmor")).setExecutor(new PlotArmorCommand());
        Objects.requireNonNull(getCommand("nick")).setExecutor(new NickCommand());
        Objects.requireNonNull(getCommand("skin")).setExecutor(new SkinCommand());
        Objects.requireNonNull(getCommand("setrandompersona")).setExecutor(new SetRandomPersonaCommand());

        enabled = true;
    }

    @Override
    public void onDisable() {
        if(!enabled) return;
        // Plugin shutdown logic
        YAML.savePlayerData(PLAYERS);
    }
}
