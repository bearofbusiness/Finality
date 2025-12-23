package org.fracturedsmp.finality.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.fracturedsmp.finality.Finality;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PlayerData implements ConfigurationSerializable {
    @Accessors(fluent = true)
    private boolean isDead;
    private UUID uuid;
    @Accessors(fluent = true)
    private boolean hasPlotArmor;
    private String personaName;
    private int deaths;
    private long lastDeathTime;

    public PlayerData(ConfigurationSection config) {
        for(String key : config.getKeys(false)) {
            switch (key) {
                case "is-dead":
                    this.isDead = config.getBoolean(key);
                    break;
                case "plot-armor":
                    this.hasPlotArmor = config.getBoolean(key);
                    break;
                case "persona-name":
                    this.personaName = config.getString(key);
                    break;
                case "deaths":
                    this.deaths = config.getInt(key);
                    break;
                case "last-death-time":
                    this.lastDeathTime = config.getLong(key);
                    break;
                case "uuid":
                    if(config.getString(key) == null) {
                        Finality.INSTANCE.getLogger().warning("UUID is null in player data!");
                        break;
                    }
                    this.uuid = UUID.fromString(Objects.requireNonNull(config.getString(key)));
                    break;
                default:
                    Finality.INSTANCE.getLogger().warning("Unknown key in player data: " + key);
            }
        }
    }


    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("is-dead", isDead,
                "plot-armor", hasPlotArmor,
                "persona-name", personaName,
                "deaths", deaths,
                "last-death-time", lastDeathTime,
                "uuid", uuid.toString()
        );
    }
}