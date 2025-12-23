package org.fracturedsmp.finality.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;

public class PlayerDataListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Initialize player data if not present
        if (Finality.PLAYERS.get(player.getUniqueId().toString()) == null) {
            Finality.PLAYERS.put(
                    player.getUniqueId().toString(),
                    new PlayerData(
                        false,
                        player.getUniqueId(),
                        false,
                        player.getName(),
                        0,
                        -1L
                    )
            );
        } else if (!Finality.PLAYERS.get(player.getUniqueId().toString()).isDead()) {
            //TODO: set skin and give new nick
        }
    }
}
