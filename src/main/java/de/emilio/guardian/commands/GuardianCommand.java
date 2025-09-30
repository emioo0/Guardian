package de.emilio.guardian.commands;

import de.emilio.guardian.gui.ReportGui;
import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.manager.ReportManager;
import de.emilio.guardian.model.Report;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuardianCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager config;
    private final ReportManager reports;
    private final ReportGui gui;

    public GuardianCommand(ConfigManager config, ReportManager reports, ReportGui gui) {
        this.config = config;
        this.reports = reports;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sendHelp(sender, label);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("guardian.admin")) { MessageUtil.sendPrefixed(sender, config.getNoPermission()); return true; }
                config.reload();
                MessageUtil.sendPrefixed(sender, config.getReloadMessage());
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("guardian.admin")) { MessageUtil.sendPrefixed(sender, config.getNoPermission()); return true; }
                List<Report> open = reports.getOpenReports();
                if (open == null || open.isEmpty()) {
                    MessageUtil.sendPrefixed(sender, config.getNoOpenReports());
                    return true;
                }
                MessageUtil.sendPrefixed(sender, config.getListHeader().replace("%count%", String.valueOf(open.size())));
                for (Report r : open) {
                    String line = config.getListFormat()
                            .replace("%id%", String.valueOf(r.getId()))
                            .replace("%reporter%", safe(r.getReporterName()))
                            .replace("%reported%", safe(r.getReportedName()))
                            .replace("%reason%", safe(r.getReason()));
                    MessageUtil.sendPrefixed(sender, line);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("history")) {
                if (!sender.hasPermission("guardian.admin")) { MessageUtil.sendPrefixed(sender, config.getNoPermission()); return true; }
                if (args.length != 2) { MessageUtil.sendPrefixed(sender, config.getUsageHistory()); return true; }
                String name = args[1];
                List<Report> hist = reports.getHistoryByReportedName(name);
                if (hist.isEmpty()) {
                    MessageUtil.sendPrefixed(sender, config.getHistoryEmpty());
                    return true;
                }
                MessageUtil.sendPrefixed(sender, config.getHistoryHeader()
                        .replace("%player%", name)
                        .replace("%count%", String.valueOf(hist.size())));
                for (Report r : hist) {
                    String line = config.getHistoryLine()
                            .replace("%id%", String.valueOf(r.getId()))
                            .replace("%reporter%", safe(r.getReporterName()))
                            .replace("%reason%", safe(r.getReason()))
                            .replace("%time%", reports.formatTimestamp(r.getTimestamp()))
                            .replace("%status%", safe(r.getStatus()));
                    MessageUtil.sendPrefixed(sender, line);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("complete")) {
                if (!sender.hasPermission("guardian.admin")) { MessageUtil.sendPrefixed(sender, config.getNoPermission()); return true; }
                if (args.length != 2) { MessageUtil.sendPrefixed(sender, config.getUsageComplete()); return true; }
                int id;
                try { id = Integer.parseInt(args[1]); }
                catch (NumberFormatException nfe) { MessageUtil.sendPrefixed(sender, config.getNotFound()); return true; }
                boolean ok = reports.complete(id);
                if (!ok) { MessageUtil.sendPrefixed(sender, config.getNotFound()); return true; }
                MessageUtil.sendPrefixed(sender, config.getCompletedMessageToAdmin().replace("%id%", String.valueOf(id)));
                return true;
            }

            sendHelp(sender, label);
            return true;

        } catch (Throwable t) {
            Bukkit.getLogger().warning("[Guardian] Command error: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            MessageUtil.sendPrefixed(sender, config.getNoOpenReports());
            return true;
        }
    }

    private String safe(String s) { return s == null ? "Unknown" : s; }

    private void sendHelp(CommandSender sender, String label) {
        MessageUtil.sendPrefixed(sender, "&7Verfügbare Befehle:");
        MessageUtil.sendPrefixed(sender, "&f/" + label + " list &7- offene Reports anzeigen");
        MessageUtil.sendPrefixed(sender, "&f/" + label + " history <spieler> &7- Historie für Spieler");
        MessageUtil.sendPrefixed(sender, "&f/" + label + " complete <id> &7- Report schließen");
        MessageUtil.sendPrefixed(sender, "&f/" + label + " reload &7- Konfiguration neu laden");
        MessageUtil.sendPrefixed(sender, "&f/" + label + " help &7- Hilfe anzeigen");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String s = args[0].toLowerCase();
            if ("list".startsWith(s)) out.add("list");
            if ("history".startsWith(s)) out.add("history");
            if ("complete".startsWith(s)) out.add("complete");
            if ("reload".startsWith(s)) out.add("reload");
            if ("help".startsWith(s)) out.add("help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("history")) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.getName().toLowerCase().startsWith(args[1].toLowerCase())) out.add(pl.getName());
            }
        }
        return out;
    }
}
