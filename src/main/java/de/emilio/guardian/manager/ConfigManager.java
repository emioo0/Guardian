package de.emilio.guardian.manager;

import de.emilio.guardian.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration cfg;

    private String prefix;

    private String notifyOpsJoin;
    private String newReportNotify;

    private String listHeader;
    private String listFormat;
    private String createdMessage;
    private String completedMessageToAdmin;
    private String completedMessageToReporter;
    private String reloadMessage;
    private String noPermission;
    private String usageReport;
    private String usageComplete;
    private String usageHistory;
    private String notFound;
    private String noOpenReports;
    private String playerNotOnlineForGui;

    private String storageFileName;

    private String guiTitle;
    private int guiSize;

    private int cooldownHours;
    private String cooldownMessage;
    private String historyHeader;
    private String historyLine;
    private String historyEmpty;

    private Map<String, ReasonItem> reasons;

    public static class ReasonItem {
        public final String id;
        public final String name;
        public final String material;
        public final List<String> lore;
        public ReasonItem(String id, String name, String material, List<String> lore) {
            this.id = id;
            this.name = name;
            this.material = material;
            this.lore = lore;
        }
    }

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();

        prefix = cfg.getString("messages.prefix", "&f[&6&lɢᴜᴀʀᴅɪᴀɴ&f]");

        notifyOpsJoin = cfg.getString("messages.notify-ops-join",
                "&eEs gibt &f%count% &eoffene Reports. &7[&aKlicken zum Anzeigen&7]");
        newReportNotify = cfg.getString("messages.new-report-op",
                "&eNeuer Report &f#%id% &egegen &c%reported% &8| &fvon &a%reporter% &8| &fGrund: &6%reason% &7[&aKlicken&7]");

        listHeader = cfg.getString("messages.list-header",
                "&6&lOffene Reports (&f%count%&6&l):");
        listFormat = cfg.getString("messages.list-format",
                "&7#&e%id% &8| &fReporter: &a%reporter% &8| &fReported: &c%reported% &8| &fGrund: &6%reason%");
        createdMessage = cfg.getString("messages.created",
                "&aDein Report wurde erstellt. &7(ID: &f#%id%&7)");
        completedMessageToAdmin = cfg.getString("messages.completed-admin",
                "&aReport &f#%id% &awurde geschlossen.");
        completedMessageToReporter = cfg.getString("messages.completed-reporter",
                "&aDein Report &f#%id% &awurde bearbeitet.");
        reloadMessage = cfg.getString("messages.reload", "&aKonfiguration neu geladen.");
        noPermission = cfg.getString("messages.no-permission", "&cDazu hast du keine Rechte.");
        usageReport = cfg.getString("messages.usage-report", "&7Verwendung: &f/report <spieler>");
        usageComplete = cfg.getString("messages.usage-complete", "&7Verwendung: &f/guardian complete <id>");
        usageHistory = cfg.getString("messages.usage-history", "&7Verwendung: &f/guardian history <spieler>");
        notFound = cfg.getString("messages.not-found", "&cKein Report mit dieser ID.");
        noOpenReports = cfg.getString("messages.no-open-reports", "&7Es gibt aktuell keine offenen Reports.");
        playerNotOnlineForGui = cfg.getString("messages.player-not-online", "&cSpieler nicht online.");

        storageFileName = cfg.getString("storage.file", "reports.yml");

        guiTitle = cfg.getString("gui.title", "&6&lWähle einen Grund");
        guiSize = cfg.getInt("gui.size", 27);

        cooldownHours = cfg.getInt("cooldown.hours", 2);
        cooldownMessage = cfg.getString("cooldown.message", "&cDu kannst erst in &f%time% &cwieder reporten.");

        historyHeader = cfg.getString("history.header", "&6&lReport-Historie für &f%player% &6&l(&f%count%&6&l):");
        historyLine = cfg.getString("history.line", "&7#&e%id% &8| &fReporter: &a%reporter% &8| &fGrund: &6%reason% &8| &fZeit: &7%time% &8| &fStatus: &7%status%");
        historyEmpty = cfg.getString("history.empty", "&7Keine Einträge für diesen Spieler.");

        reasons = new LinkedHashMap<>();
        ConfigurationSection sec = cfg.getConfigurationSection("reasons");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                String path = "reasons." + key + ".";
                String name = cfg.getString(path + "name", key);
                String mat = cfg.getString(path + "material", "PAPER");
                List<String> lore = cfg.getStringList(path + "lore");
                reasons.put(key.toUpperCase(), new ReasonItem(key.toUpperCase(), name, mat, lore));
            }
        }

        plugin.saveConfig();
    }

    public void reload() { load(); }
    public FileConfiguration raw() { return cfg; }

    public String getPrefix() { return prefix; }

    public String getNotifyOpsJoin() { return notifyOpsJoin; }
    public String getNewReportNotify() { return newReportNotify; }

    public String getListHeader() { return listHeader; }
    public String getListFormat() { return listFormat; }
    public String getCreatedMessage() { return createdMessage; }
    public String getCompletedMessageToAdmin() { return completedMessageToAdmin; }
    public String getCompletedMessageToReporter() { return completedMessageToReporter; }
    public String getReloadMessage() { return reloadMessage; }
    public String getNoPermission() { return noPermission; }
    public String getUsageReport() { return usageReport; }
    public String getUsageComplete() { return usageComplete; }
    public String getUsageHistory() { return usageHistory; }
    public String getNotFound() { return notFound; }
    public String getNoOpenReports() { return noOpenReports; }
    public String getPlayerNotOnlineForGui() { return playerNotOnlineForGui; }

    public String getStorageFileName() { return storageFileName; }

    public String getGuiTitle() { return guiTitle; }
    public int getGuiSize() { return guiSize; }

    public int getCooldownHours() { return cooldownHours; }
    public String getCooldownMessage() { return cooldownMessage; }

    public String getHistoryHeader() { return historyHeader; }
    public String getHistoryLine() { return historyLine; }
    public String getHistoryEmpty() { return historyEmpty; }

    public Map<String, ReasonItem> getReasons() { return reasons; }
}
