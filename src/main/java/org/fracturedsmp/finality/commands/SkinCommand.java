package org.fracturedsmp.finality.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;
import java.util.stream.Stream;

public class SkinCommand implements TabExecutor {

    private static final String PERM = "finality.skin.admin";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
        if (!sender.hasPermission(PERM) && !sender.isOp()) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("""
                        /skin set <player> <skinName> - Set the player's skin to the skin of the specified username.
                        /skin get <player> - Get the stored persona skin name for the player.
                        /skin reset <player> - Reset the player's skin to their original skin.
                        /skin setpersona <player> - Apply the stored persona skin for the player.
                        /skin getpersona <player> - Get the stored persona skin name for the player.(same thing a get)
                        """);
            return true;
        }

        String sub = args[0].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("Player not online: " + args[1]);
            return true;
        }

        PlayerData data = Finality.PLAYERS.get(target.getUniqueId().toString());
        if (data == null) {
            sender.sendMessage("No PlayerData for " + target.getName());
            return true;
        }

        switch (sub) {
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /skin set <player> <skinName>");
                    return true;
                }
                String skinName = args[2].trim();
                if (skinName.isBlank()) {
                    sender.sendMessage("skinName cannot be blank.");
                    return true;
                }

                data.setSkinName(skinName);
                applySkin(target, skinName);

                sender.sendMessage("Set skin for " + target.getName() + " -> " + skinName);
                target.sendMessage("Your skin was set to: " + skinName);
                return true;
            }

            case "get" -> {
                sender.sendMessage(target.getName() + " stored personaSkinName: " +
                        ((data.getSkinName() == null || data.getSkinName().isBlank()) ? "-" : data.getSkinName()));
                return true;
            }

            case "reset" -> {
                data.setSkinName(null);
                NickAPI.resetSkin(target);
                NickAPI.refreshPlayer(target);

                sender.sendMessage("Reset skin for " + target.getName());
                target.sendMessage("Your skin was reset.");
                return true;
            }

            case "setpersona" -> {
                String skinName = data.getSkinName();
                if (skinName == null || skinName.isBlank()) {
                    sender.sendMessage("No persona skin stored for " + target.getName());
                    return true;
                }

                applySkin(target, skinName);
                sender.sendMessage("Applied persona skin for " + target.getName() + " -> " + skinName);
                target.sendMessage("Your skin was set to your persona skin: " + skinName);
                return true;
            }

            case "getpersona" -> {
                sender.sendMessage(target.getName() + " personaSkinName: " +
                        ((data.getSkinName() == null || data.getSkinName().isBlank()) ? "-" : data.getSkinName()));
                return true;
            }

            default -> {
                sender.sendMessage("""
                        /skin set <player> <skinName> - Set the player's skin to the skin of the specified username.
                        /skin get <player> - Get the stored persona skin name for the player.
                        /skin reset <player> - Reset the player's skin to their original skin.
                        /skin setpersona <player> - Apply the stored persona skin for the player.
                        /skin getpersona <player> - Get the stored persona skin name for the player.(same thing a get)
                        """);
                return true;
            }
        }
    }

    private static void applySkin(Player target, String skinName) {
        NickAPI.setSkin(target, skinName);
        NickAPI.refreshPlayer(target);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NonNull Command command, @NonNull String alias, String @NonNull [] args) {
        if (!sender.hasPermission(PERM) && !sender.isOp()) return List.of();

        String partial = args.length == 0 ? "" : args[args.length - 1].toLowerCase();

        if (args.length == 1) {
            return Stream.of("set", "get", "reset", "setpersona", "getpersona", "help")
                    .filter(s -> s.startsWith(partial))
                    .toList();
        }

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(partial))
                    .sorted()
                    .toList();
        }
        return List.of();
    }
}
