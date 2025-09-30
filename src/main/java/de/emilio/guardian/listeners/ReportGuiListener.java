package de.emilio.guardian.listeners;

import de.emilio.guardian.gui.ReportGui;
import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.manager.CooldownManager;
import de.emilio.guardian.manager.ReportManager;
import de.emilio.guardian.model.Report;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ReportGuiListener implements Listener {

    private final ConfigManager config;
    private final ReportManager reports;
    private final ReportGui gui;
    private final CooldownManager cooldowns;

    public ReportGuiListener(ConfigManager config, ReportManager reports, ReportGui gui, CooldownManager cooldowns) {
        this.config = config;
        this.reports = reports;
        this.gui = gui;
        this.cooldowns = cooldowns;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView() == null || e.getView().title() == null) return;

        String openTitle = MessageUtil.toLegacySection(config.getGuiTitle());
        String viewTitle = MessageUtil.SEC.serialize(e.getView().title());
        if (!openTitle.equals(viewTitle)) return;

        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        String reason = e.getCurrentItem().getItemMeta()
                .getPersistentDataContainer().get(gui.getReasonKey(), PersistentDataType.STRING);
        String targetUuid = e.getCurrentItem().getItemMeta()
                .getPersistentDataContainer().get(gui.getTargetKey(), PersistentDataType.STRING);
        if (reason == null || targetUuid == null) return;

        UUID target = UUID.fromString(targetUuid);
        Player reported = Bukkit.getPlayer(target);
        String reportedName = reported != null ? reported.getName() : "Unknown";

        Report r = reports.createReport(p.getUniqueId(), p.getName(), target, reportedName, reason);
        cooldowns.markReported(p.getUniqueId());
        MessageUtil.sendPrefixed(p, config.getCreatedMessage().replace("%id%", String.valueOf(r.getId())));
        p.closeInventory();
    }
}
