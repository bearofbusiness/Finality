package org.fracturedsmp.finality.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;
import xyz.haoshoku.nick.api.NickAPI;

public class PlayerDataListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String key = player.getUniqueId().toString();
        PlayerData data = Finality.PLAYERS.get(key);
        // Initialize player data if not present
        if (data == null) {
            Finality.PLAYERS.put(
                    player.getUniqueId().toString(),
                    new PlayerData(
                        false,
                        player.getUniqueId(),
                        false,
                        player.getName(),
                        player.getName(),
                        0,
                        -1L
                    )
            );
            data = Finality.PLAYERS.get(key);
        }

        if (player.hasPermission("finality.keepidentity")) {
            // reset to real
            NickAPI.resetNick(player);
            NickAPI.resetSkin(player);
            NickAPI.resetProfileName(player);
            NickAPI.refreshPlayer(player);
            player.displayName(Component.text(player.getName()));
            player.playerListName(Component.text(player.getName()));
            player.customName(null);
            player.setCustomNameVisible(true);
            return;
        }

        String personaName = data.getPersonaName();
        //catch all
        if (personaName != null && !personaName.isBlank() && !personaName.equals(player.getName())) {
            NickAPI.setNick(player, personaName);
            NickAPI.setProfileName(player, personaName);

            player.displayName(Component.text(personaName));
            player.playerListName(Component.text(personaName));
            player.customName(Component.text(personaName));
            player.setCustomNameVisible(true);

        } else {
            // ensure real name is stored if personaName is blank for some reason
            NickAPI.resetNick(player);
            NickAPI.resetProfileName(player);
        }

        String personaSkin = data.getSkinName();
        if (personaSkin != null && !personaSkin.isBlank() && !personaSkin.equals(player.getName())) {
            NickAPI.setSkin(player, personaSkin);
        } else {
            // ensure real skin is stored if personaSkin is blank for some reason
            NickAPI.resetSkin(player);
        }

        NickAPI.refreshPlayer(player);
    }
}
