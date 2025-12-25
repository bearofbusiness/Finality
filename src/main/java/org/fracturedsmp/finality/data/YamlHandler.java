package org.fracturedsmp.finality.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.fracturedsmp.finality.util.config.ConfigFile;

import java.util.HashMap;
import java.util.Map;

public class YamlHandler {
    JavaPlugin plugin;
    ConfigFile playerDataFile;



    public YamlHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerDataFile = new ConfigFile(plugin, "playerdata");
        this.playerDataFile.load();
    }

    public HashMap<String, PlayerData> loadPlayerData() {
        ConfigurationSection playerData = playerDataFile.getConfigurationSection("playerdata");
        if(playerData != null) {
            return (HashMap<String, PlayerData>) playerData.getValues(false).entrySet().stream().collect(
                    java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new PlayerData((ConfigurationSection) entry.getValue())
                    )
            );
        }
        return new HashMap<>();
    }

    public void savePlayerData(Map<String, PlayerData> playerData) {
        for(Map.Entry<String, PlayerData> entry : playerData.entrySet()) {
            playerDataFile.set("playerdata." + entry.getKey(), entry.getValue().serialize());
        }
        playerDataFile.save();
    }
}
