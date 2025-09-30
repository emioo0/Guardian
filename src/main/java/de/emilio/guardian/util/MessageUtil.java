package de.emilio.guardian.util;

import de.emilio.guardian.manager.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static ConfigManager config;


    public static final LegacyComponentSerializer AMP = LegacyComponentSerializer.legacyAmpersand();
    public static final LegacyComponentSerializer SEC = LegacyComponentSerializer.legacySection();

    public static void init(ConfigManager cfg) { config = cfg; }

    public static Component legacyComponent(String s) {
        if (s == null) return Component.empty();
        return AMP.deserialize(s);
    }

    public static String toLegacySection(String s) {
        return SEC.serialize(AMP.deserialize(s));
    }

    public static void sendPrefixed(CommandSender sender, String raw) {
        String full = config.getPrefix() + " " + (raw == null ? "" : raw);

        sender.sendMessage(toLegacySection(full));
    }

    public static void broadcastPrefixed(String raw) {
        String msg = toLegacySection(config.getPrefix() + " " + (raw == null ? "" : raw));
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(msg));
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void sendClickableToOps(String text, String command) {

        Component comp = legacyComponent(config.getPrefix() + " " + (text == null ? "" : text))
                .clickEvent(ClickEvent.runCommand(command));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) {
                try { p.sendMessage(comp); }  // Paper/neu
                catch (Throwable t) { p.sendMessage(toLegacySection(config.getPrefix() + " " + text)); }
            }
        }
        Bukkit.getConsoleSender().sendMessage(toLegacySection(config.getPrefix() + " " + text));
    }
}
