package de.emilio.guardian.gui;

import de.emilio.guardian.Main;
import de.emilio.guardian.manager.ConfigManager;
import de.emilio.guardian.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportGui {

    private final NamespacedKey reasonKey;
    private final NamespacedKey targetKey;
    private final ConfigManager config;

    private static final List<Integer> PREFERRED_SLOTS = Arrays.asList(
            10,11,12,13,14,15,16,17,
            19,20,21,22,23,24,25,26,
            1,2,3,4,5,6,7,8
    );

    public ReportGui(Main plugin, ConfigManager config) {
        this.config = config;
        this.reasonKey = new NamespacedKey(plugin, "reason");
        this.targetKey = new NamespacedKey(plugin, "target");
    }

    public void openFor(Player opener, Player target) {
        if (target == null || !target.isOnline()) {
            MessageUtil.sendPrefixed(opener, config.getPlayerNotOnlineForGui());
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.legacyComponent(config.getGuiTitle()));

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        fMeta.displayName(MessageUtil.legacyComponent("&7"));
        fMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        filler.setItemMeta(fMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        bMeta.displayName(MessageUtil.legacyComponent("&0"));
        bMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        border.setItemMeta(bMeta);
        inv.setItem(0, border);
        inv.setItem(9, border);
        inv.setItem(18, border);

        List<ConfigManager.ReasonItem> list = new ArrayList<>(config.getReasons().values());
        int idx = 0;
        for (ConfigManager.ReasonItem r : list) {
            if (idx >= PREFERRED_SLOTS.size()) break;
            int slot = PREFERRED_SLOTS.get(idx++);

            Material mat = Material.matchMaterial(r.material) != null ? Material.matchMaterial(r.material) : Material.PAPER;
            ItemStack it = new ItemStack(mat);
            ItemMeta meta = it.getItemMeta();
            meta.displayName(MessageUtil.legacyComponent("&l" + r.name));

            List<net.kyori.adventure.text.Component> advLore = new ArrayList<>();
            advLore.add(MessageUtil.legacyComponent("&8Target: &7" + target.getName()));
            if (r.lore != null) {
                for (String s : r.lore) advLore.add(MessageUtil.legacyComponent(s));
            }
            meta.lore(advLore);

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

            meta.getPersistentDataContainer().set(reasonKey, PersistentDataType.STRING, r.id);
            meta.getPersistentDataContainer().set(targetKey, PersistentDataType.STRING, target.getUniqueId().toString());

            it.setItemMeta(meta);
            inv.setItem(slot, it);
        }

        opener.openInventory(inv);
    }

    public NamespacedKey getReasonKey() { return reasonKey; }
    public NamespacedKey getTargetKey() { return targetKey; }
}
