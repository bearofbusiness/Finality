package org.fracturedsmp.finality.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;

public class PlotArmorListener implements Listener {
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        PlayerData deceasedData = Finality.PLAYERS.get(deceased.getUniqueId().toString());

        if(deceasedData != null) {
            if(deceasedData.hasPlotArmor()) {
                event.setCancelled(true);
            }
        } else {
            Finality.INSTANCE.getLogger().warning("PlayerData missing for deceased player in PlayerDeathEvent.");
            Finality.INSTANCE.getLogger().warning("Deceased UUID: " + deceased.getUniqueId().toString());
        }
    }
}
