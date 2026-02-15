package org.fracturedsmp.finality.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.fracturedsmp.finality.Finality;
import org.fracturedsmp.finality.data.PlayerData;

public class CommandNickRewriteListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCmd(PlayerCommandPreprocessEvent e) {

        String msg = e.getMessage();

        // allow bypass with /- prefix, since some plugins might not work with rewritten commands
        if (msg != null && msg.startsWith("/-")) {
            e.setMessage("/" + msg.substring(2)); // strip the '-'
            return;
        }

        String rewritten = rewrite(msg);
        if (rewritten != null) e.setMessage(rewritten);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsoleCmd(ServerCommandEvent e) {
        String cmd = e.getCommand();

        if (cmd != null && cmd.startsWith("-")) {
            e.setCommand(cmd.substring(1)); // strip the '-'
            return;
        }

        String rewritten = rewrite("/" + cmd);
        if (rewritten != null) e.setCommand(rewritten.substring(1));
    }

    private String rewrite(String msg) {
        if (msg == null || !msg.startsWith("/")) return null;

        // cmd target [rest...]
        String[] parts = msg.substring(1).split(" ", 3);
        if (parts.length < 2) return null;

        String cmd = parts[0].toLowerCase();
        // handle aliases like minecraft:kick
        if (cmd.contains(":")) cmd = cmd.substring(cmd.indexOf(':') + 1);

        // only rewrite commands you care about
        if (!cmd.equals("kick") && !cmd.equals("ban") && !cmd.equals("pardon") && !cmd.equals("tp") && !cmd.equals("tell"))
            return null;

        String token = parts[1];

        Player resolved = resolveByNameOrPersona(token);
        if (resolved == null) return null;

        String real = resolved.getName();
        if (token.equalsIgnoreCase(real)) return null;

        String rest = (parts.length == 3) ? " " + parts[2] : "";
        return "/" + parts[0] + " " + real + rest;
    }

    private Player resolveByNameOrPersona(String token) {
        Player exact = Bukkit.getPlayerExact(token);
        if (exact != null) return exact;

        Player found = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData d = Finality.PLAYERS.get(p.getUniqueId().toString());
            if (d != null && d.getPersonaName() != null && token.equalsIgnoreCase(d.getPersonaName())) {
                if (found != null) return null; // ambiguous do not rewrite
                found = p;
            }
        }
        return found;
    }
}