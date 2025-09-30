package de.emilio.guardian;

import de.emilio.guardian.commands.GuardianCommand;
import de.emilio.guardian.commands.ReportCommand;
import de.emilio.guardian.gui.ReportGui;
import de.emilio.guardian.listeners.JoinListener;
import de.emilio.guardian.listeners.ReportGuiListener;
import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.manager.CooldownManager;
import de.emilio.guardian.manager.ReportManager;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private ReportManager reportManager;
    private CooldownManager cooldownManager;
    private ReportGui reportGui;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.load();

        MessageUtil.init(configManager);

        reportManager = new ReportManager(this, configManager);
        reportManager.load();

        cooldownManager = new CooldownManager(this, configManager);
        cooldownManager.load();

        reportGui = new ReportGui(this, configManager);

        Bukkit.getPluginManager().registerEvents(new ReportGuiListener(configManager, reportManager, reportGui, cooldownManager), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(configManager, reportManager), this);

        GuardianCommand guardian = new GuardianCommand(configManager, reportManager, reportGui);
        if (getCommand("guardian") != null) {
            getCommand("guardian").setExecutor(guardian);
            getCommand("guardian").setTabCompleter(guardian);
        }
        ReportCommand report = new ReportCommand(configManager, reportGui, cooldownManager);
        if (getCommand("report") != null) {
            getCommand("report").setExecutor(report);
            getCommand("report").setTabCompleter(report);
        }
    }

    @Override
    public void onDisable() {
        if (reportManager != null) reportManager.save();
        if (cooldownManager != null) cooldownManager.save();
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public ReportGui getReportGui() {
        return reportGui;
    }
}
