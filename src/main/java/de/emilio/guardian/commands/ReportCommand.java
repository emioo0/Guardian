package de.emilio.guardian.commands;

import de.emilio.guardian.gui.ReportGui;
import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.manager.CooldownManager;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager config;
    private final ReportGui gui;
    private final CooldownManager cooldowns;

    public ReportCommand(ConfigManager config, ReportGui gui, CooldownManager cooldowns) {
        this.config = config;
        this.gui = gui;
        this.cooldowns = cooldowns;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            MessageUtil.sendPrefixed(sender, "&cNur im Spiel verwendbar.");
            return true;
        }
        if (args.length != 1) {
            MessageUtil.sendPrefixed(p, config.getUsageReport());
            return true;
        }

        if (!cooldowns.canReport(p.getUniqueId())) {
            long ms = cooldowns.remainingMillis(p.getUniqueId());
            long h = TimeUnit.MILLISECONDS.toHours(ms);
            long m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60;
            String time = String.format("%02dh %02dm", h, m);
            MessageUtil.sendPrefixed(p, config.getCooldownMessage().replace("%time%", time));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
        Player online = target != null && target.isOnline() ? target.getPlayer() : Bukkit.getPlayerExact(args[0]);
        if (online == null) {
            MessageUtil.sendPrefixed(p, config.getPlayerNotOnlineForGui());
            return true;
        }
        gui.openFor(p, online);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.getName().toLowerCase().startsWith(args[0].toLowerCase())) out.add(pl.getName());
            }
        }
        return out;
    }
}
