package org.fracturedsmp.finality.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.fracturedsmp.finality.util.config.ConfigFile;

import java.util.List;
import java.util.Random;

public class SkinPool {
    private final ConfigFile file;
    private final Random rng = new Random();

    public SkinPool(JavaPlugin plugin) {
        this.file = new ConfigFile(plugin, "skins");
    }

    public String randomSkinName() {
        List<String> pool = file.getStringList("pool");
        if (pool.isEmpty()) return null;
        return pool.get(rng.nextInt(pool.size()));
    }
}
