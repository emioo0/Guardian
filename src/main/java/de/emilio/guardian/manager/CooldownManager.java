package de.emilio.guardian.manager;

import de.emilio.guardian.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Main plugin;
    private final ConfigManager config;
    private File file;
    private FileConfiguration data;
    private final Map<UUID, Long> lastReport = new HashMap<>();

    public CooldownManager(Main plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "cooldowns.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        data = YamlConfiguration.loadConfiguration(file);
        if (data.isConfigurationSection("last")) {
            for (String key : data.getConfigurationSection("last").getKeys(false)) {
                try {
                    UUID u = UUID.fromString(key);
                    long t = data.getLong("last." + key, 0L);
                    lastReport.put(u, t);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Long> e : lastReport.entrySet()) {
            data.set("last." + e.getKey().toString(), e.getValue());
        }
        try { data.save(file); } catch (IOException ignored) {}
    }

    public boolean canReport(UUID uuid) {
        long now = System.currentTimeMillis();
        long last = lastReport.getOrDefault(uuid, 0L);
        long cd = config.getCooldownHours() * 3600_000L;
        return now - last >= cd;
    }

    public long remainingMillis(UUID uuid) {
        long now = System.currentTimeMillis();
        long last = lastReport.getOrDefault(uuid, 0L);
        long cd = config.getCooldownHours() * 3600_000L;
        long rem = cd - (now - last);
        return Math.max(0L, rem);
    }

    public void markReported(UUID uuid) {
        lastReport.put(uuid, System.currentTimeMillis());
        save();
    }
}
