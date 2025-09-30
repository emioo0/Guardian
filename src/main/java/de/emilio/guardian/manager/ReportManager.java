package de.emilio.guardian.manager;

import de.emilio.guardian.Main;
import de.emilio.guardian.model.Report;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportManager {

    private final Main plugin;
    private final ConfigManager config;
    private File file;
    private FileConfiguration data;
    private int nextId;

    public ReportManager(Main plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), config.getStorageFileName());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        data = YamlConfiguration.loadConfiguration(file);
        nextId = Math.max(1, data.getInt("next-id", 1));
        if (!data.isConfigurationSection("reports")) {
            data.createSection("reports");
            save();
        }
    }

    public void save() {
        data.set("next-id", Math.max(1, nextId));
        try { data.save(file); } catch (IOException ignored) {}
    }

    public Report createReport(UUID reporter, String reporterName, UUID reported, String reportedName, String reason) {
        int id = nextId++;
        long ts = System.currentTimeMillis();
        Report r = new Report(id, reporter, safe(reporterName), reported, safe(reportedName), safe(reason), ts, "open");
        data.set("reports." + id, r.serialize());
        save();

        String txt = config.getNewReportNotify()
                .replace("%id%", String.valueOf(id))
                .replace("%reporter%", r.getReporterName())
                .replace("%reported%", r.getReportedName())
                .replace("%reason%", r.getReason());
        MessageUtil.sendClickableToOps(txt, "/guardian list");

        return r;
    }

    public boolean complete(int id) {
        if (!data.isConfigurationSection("reports." + id)) return false;
        Map<String, Object> m = Objects.requireNonNull(data.getConfigurationSection("reports." + id)).getValues(false);
        Report r = safeDeserialize(id, m);
        if (r == null) return false;
        r.setStatus("closed");
        data.set("reports." + id, r.serialize());
        save();
        OfflinePlayer reporter = Bukkit.getOfflinePlayer(r.getReporterUuid());
        if (reporter.isOnline()) {
            MessageUtil.sendPrefixed(reporter.getPlayer(), config.getCompletedMessageToReporter().replace("%id%", String.valueOf(id)));
        }
        return true;
    }

    public List<Report> getOpenReports() {
        List<Report> list = new ArrayList<>();
        if (!data.isConfigurationSection("reports")) return list;
        for (String key : Objects.requireNonNull(data.getConfigurationSection("reports")).getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                Map<String, Object> m = Objects.requireNonNull(data.getConfigurationSection("reports." + key)).getValues(false);
                Report r = safeDeserialize(id, m);
                if (r != null && "open".equalsIgnoreCase(r.getStatus())) list.add(r);
            } catch (Throwable t) {
                Bukkit.getLogger().warning("[Guardian] Ignoring malformed report entry: " + key + " (" + t.getMessage() + ")");
            }
        }
        list.sort(Comparator.comparingInt(Report::getId));
        return list;
    }

    public List<Report> getHistoryByReportedName(String playerName) {
        List<Report> list = new ArrayList<>();
        if (!data.isConfigurationSection("reports")) return list;
        for (String key : Objects.requireNonNull(data.getConfigurationSection("reports")).getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                Map<String, Object> m = Objects.requireNonNull(data.getConfigurationSection("reports." + key)).getValues(false);
                Report r = safeDeserialize(id, m);
                if (r != null && r.getReportedName() != null && r.getReportedName().equalsIgnoreCase(playerName)) list.add(r);
            } catch (Throwable ignored) {}
        }
        list.sort(Comparator.comparingInt(Report::getId));
        return list;
    }

    private Report safeDeserialize(int id, Map<String,Object> m) {
        try {
            return Report.deserialize(id, m);
        } catch (Throwable t) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "Unknown" : s; }

    public String formatTimestamp(long ts) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ts));
    }

    public Report get(int id) {
        if (!data.isConfigurationSection("reports." + id)) return null;
        Map<String, Object> m = Objects.requireNonNull(data.getConfigurationSection("reports." + id)).getValues(false);
        return safeDeserialize(id, m);
    }
}
