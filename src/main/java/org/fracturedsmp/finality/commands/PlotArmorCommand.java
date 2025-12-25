package org.fracturedsmp.finality.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.fracturedsmp.finality.Finality;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class PlotArmorCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // Permission check
        if(sender instanceof Player player) {
            if(!player.hasPermission("finality.plotarmor") || !player.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /plotarmor <give|remove|status> <player>");
            return true;
        }

        String action = args[0];
        String targetPlayerName = args[1];
        Player targetPlayer = Finality.INSTANCE.getServer().getPlayerExact(targetPlayerName);

        if (targetPlayer == null) {
            sender.sendMessage("Player " + targetPlayerName + " not found.");
            return true;
        }

        String targetUUID = targetPlayer.getUniqueId().toString();
        var playerData = Finality.PLAYERS.get(targetUUID);
        if (playerData == null) {
            sender.sendMessage("No data found for player " + targetPlayerName + ".");
            return true;
        }

        switch (action.toLowerCase()) {
            case "give" -> {
                playerData.hasPlotArmor(true);
                sender.sendMessage("Gave plot armor to " + targetPlayerName + ".");
            }
            case "remove" -> {
                playerData.hasPlotArmor(false);
                sender.sendMessage("Removed plot armor from " + targetPlayerName + ".");
            }
            case "status" -> {
                boolean hasArmor = playerData.hasPlotArmor();
                sender.sendMessage(targetPlayerName + " has plot armor: " + hasArmor);
            }
            default -> sender.sendMessage("Unknown action: " + action + ". Use give, remove, or status.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        switch (args.length) {
            case 1 -> {
                return Stream.of("give", "remove", "status").filter(s -> s.startsWith(args[0])).toList();
            }
            case 2 -> {
                return Finality.INSTANCE.getServer().getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).toList();
            }
        }
        return List.of();
    }
}
