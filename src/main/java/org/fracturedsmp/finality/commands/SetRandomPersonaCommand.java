package org.fracturedsmp.finality.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;
import org.fracturedsmp.finality.util.NameGenerator;
import org.jspecify.annotations.NonNull;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;

public class SetRandomPersonaCommand implements TabExecutor {

    private static final String PERM = "finality.persona.admin";

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String @NonNull [] args) {
        if (!sender.hasPermission(PERM) && !sender.isOp()) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /setrandompersona <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("Player not online: " + args[0]);
            return true;
        }

        String key = target.getUniqueId().toString();
        PlayerData data = Finality.PLAYERS.get(key);
        if (data == null) {
            sender.sendMessage("No PlayerData for " + target.getName() + " (join once first).");
            return true;
        }

        String nick = NameGenerator.generateUniquePersonaName(Finality.PLAYERS);
        String skinName = Finality.INSTANCE.skinPool.randomSkinName();

        data.setPersonaName(nick);
        data.setSkinName(skinName);

        applySkin(target, skinName);
        applyNick(target, nick);



        sender.sendMessage("Random persona applied to " + target.getName()
                + " -> nick=" + nick + " skin=" + (skinName == null ? "-" : skinName));
        target.sendMessage("Your persona was randomized: " + nick);
        return true;
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

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String @NonNull [] args) {
        if (!sender.hasPermission(PERM) && !sender.isOp()) return List.of();
        if (args.length == 1) {
            String p = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(p))
                    .sorted()
                    .toList();
        }
        return List.of();
    }
}
