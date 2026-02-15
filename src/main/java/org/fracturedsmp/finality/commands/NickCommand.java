package org.fracturedsmp.finality.commands;

import net.kyori.adventure.text.Component;
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

public class NickCommand implements TabExecutor {

    private static final String PERM = "finality.nick.admin";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!sender.hasPermission(PERM) && !sender.isOp()) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("""
                        /nick set <player> <name...> -> applies nick everywhere + stores personaName = <name>
                        /nick get <player> -> shows current stored persona + what is currently displayed(chopped for debug only)
                        /nick reset <player> -> resets to real name + stores persona = real name
                        /nick setpersona <player> -> applies stored personaName
                        /nick getpersona <player> -> prints stored personaName
                        /nick help -> shows this message""");
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
                    sender.sendMessage("Usage: /nick set <player> <name...>");
                    return true;
                }
                String name = join(args, 2).trim();
                if (name.isBlank()) {
                    sender.sendMessage("Name cannot be blank.");
                    return true;
                }

                data.setPersonaName(name);
                applyNickEverywhere(target, name);

                sender.sendMessage("Set nick for " + target.getName() + " -> " + name);
                target.sendMessage("Your nick was set to: " + name);
                return true;
            }
            case "get" -> {
                sender.sendMessage("Player: " + target.getName());
                sender.sendMessage("Stored personaName: " + nullToDash(data.getPersonaName()));
                sender.sendMessage("DisplayName: " + componentToPlain(target.displayName()));
                sender.sendMessage("TabName: " + componentToPlain(target.playerListName()));
                return true;
            }
            case "reset" -> {
                String original = target.getName();

                data.setPersonaName(original);
                resetNickEverywhere(target);

                sender.sendMessage("Reset nick for " + target.getName());
                target.sendMessage("Your nick was reset.");
                return true;
            }
            case "setpersona" -> {
                String persona = data.getPersonaName();
                if (persona == null || persona.isBlank()) {
                    sender.sendMessage("No persona stored for " + target.getName());
                    return true;
                }

                applyNickEverywhere(target, persona);
                sender.sendMessage("Applied persona for " + target.getName() + " -> " + persona);
                target.sendMessage("Your nick was set to your persona: " + persona);
                return true;
            }
            case "getpersona" -> {
                sender.sendMessage(target.getName() + " personaName: " + nullToDash(data.getPersonaName()));
                return true;
            }
            default -> {
                sender.sendMessage("""
                        /nick set <player> <name...> -> applies nick everywhere + stores personaName = <name>
                        /nick get <player> -> shows current stored persona + what is currently displayed(chopped for debug only)
                        /nick reset <player> -> resets to real name + stores persona = real name
                        /nick setpersona <player> -> applies stored personaName
                        /nick getpersona <player> -> prints stored personaName
                        /nick help -> shows this message""");
                return true;
            }
        }
    }

    private static void applyNickEverywhere(Player p, String nick) {

        NickAPI.setNick(p, nick);
        NickAPI.setProfileName(p, nick);
        NickAPI.refreshPlayer(p);

        p.displayName(Component.text(nick));
        p.playerListName(Component.text(nick));
        p.customName(Component.text(nick));
        p.setCustomNameVisible(true);
    }

    private static void resetNickEverywhere(Player p) {
        String original = p.getName();

        NickAPI.setNick(p, original);
        NickAPI.setProfileName(p, original);
        NickAPI.refreshPlayer(p);

        p.displayName(Component.text(original));
        p.playerListName(Component.text(original));
        p.customName(null);
        p.setCustomNameVisible(false);
    }

    private static String join(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private static String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private static String componentToPlain(Component c) {
        return c == null ? "-" : c.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
