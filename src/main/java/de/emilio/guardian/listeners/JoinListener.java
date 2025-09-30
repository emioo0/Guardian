package de.emilio.guardian.listeners;

import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.manager.ReportManager;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ConfigManager config;
    private final ReportManager reports;

    public JoinListener(ConfigManager config, ReportManager reports) {
        this.config = config;
        this.reports = reports;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp()) return;
        int count = reports.getOpenReports().size();
        if (count <= 0) return;
        String msg = config.getNotifyOpsJoin().replace("%count%", String.valueOf(count));
        MessageUtil.sendClickableToOps(msg, "/guardian list");
    }
}
