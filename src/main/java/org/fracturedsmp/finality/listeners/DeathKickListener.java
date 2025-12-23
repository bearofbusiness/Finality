package org.fracturedsmp.finality.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;

public class DeathKickListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        PlayerData deceasedData = Finality.PLAYERS.get(deceased.getUniqueId().toString());
        if(deceasedData != null) {
            deceasedData.isDead(true);
        } else {
            Finality.INSTANCE.getLogger().warning("PlayerData missing for deceased player in PlayerDeathEvent.");
            Finality.INSTANCE.getLogger().warning("Deceased UUID: " + deceased.getUniqueId());
        }
        Finality.INSTANCE.getServer().getScheduler().runTaskLater(Finality.INSTANCE, () -> {
            event.getPlayer().kick(
                    Component.text("You have been kicked due to death. Please rejoin to continue playing.")
            );
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRejoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = Finality.PLAYERS.get(player.getUniqueId().toString());
        if(playerData != null && playerData.isDead()) {
            playerData.isDead(false);
            //TODO: change skin and give new nick
        }
    }
}
