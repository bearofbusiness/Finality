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
import org.fracturedsmp.finality.util.NameGenerator;
import xyz.haoshoku.nick.api.NickAPI;

public class DeathKickListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        PlayerData deceasedData = Finality.PLAYERS.get(deceased.getUniqueId().toString());
        if(deceasedData != null) {
            deceasedData.isDead(true);
            deceasedData.setLastDeathTime(System.currentTimeMillis());
        } else {
            Finality.INSTANCE.getLogger().warning("PlayerData missing for deceased player in PlayerDeathEvent.");
            Finality.INSTANCE.getLogger().warning("Deceased UUID: " + deceased.getUniqueId());
        }
        Finality.INSTANCE.getServer().getScheduler().runTaskLater(Finality.INSTANCE, () -> {
            event.getPlayer().kick(
                    Component.text("You have been kicked due to death. Please rejoin to continue playing.")
            );
        }, 1L);

        String key = deceased.getUniqueId().toString();
        PlayerData data = Finality.PLAYERS.get(key);

        String nick = NameGenerator.generateUniquePersonaName(Finality.PLAYERS);
        String skinName = Finality.INSTANCE.skinPool.randomSkinName();

        data.setPersonaName(nick);
        data.setSkinName(skinName);

        applySkin(deceased, skinName);
        applyNick(deceased, nick);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRejoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = Finality.PLAYERS.get(player.getUniqueId().toString());
        if(playerData != null && playerData.isDead()) {
            playerData.isDead(false);
        }
    }

    private static void applySkin(Player target, String skinName) {
        NickAPI.setSkin(target, skinName);
        NickAPI.refreshPlayer(target);
    }

    private static void applyNick(Player p, String nick) {

        NickAPI.setNick(p, nick);
        NickAPI.setProfileName(p, nick);
        NickAPI.refreshPlayer(p);

        p.displayName(Component.text(nick));
        p.playerListName(Component.text(nick));
        p.customName(Component.text(nick));
        p.setCustomNameVisible(true);
    }
}
