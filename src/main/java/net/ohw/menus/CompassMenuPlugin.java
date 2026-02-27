package net.ohw.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompassMenuPlugin extends JavaPlugin implements Listener {
    private static CompassMenuPlugin instance;
    public static final String MENU_TITLE = ChatColor.DARK_AQUA + "Compass Menu";

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("CompassMenu enabled");
        // 註冊事件
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        // 註冊指令（在 plugin.yml 中也會宣告）
        this.getCommand("menu").setExecutor(new MenuCommand(this));

        // BungeeCord 插件通道
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public static CompassMenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("CompassMenu disabled");
    }

    public static final String[] SERVERS = {"pvp", "shop", "bridge", "smp-1", "lobby"};

    public static Inventory createCompassMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, MENU_TITLE);

        // 用灰色玻璃填滿邊緣作為裝飾
        ItemStack filler = createGrayGlass();
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" "); // 空白名稱避免顯示
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, filler);
            }
        }

        // 根據伺服器列表建立按鈕
        int start = 10; // 從第 10 格開始填充
        for (String server : SERVERS) {
            if (start >= 45) break; // 避免放到邊緣
            inv.setItem(start, createCompassItem(server, "點擊以連接到 " + server));
            start++;
        }

        return inv;
    }

    private static ItemStack createCompassItem(String name, String loreText) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "傳送：" + name);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + loreText);
            meta.setLore(lore);
            compass.setItemMeta(meta);
        }
        return compass;
    }

    /**
     * Returns a gray glass pane item that works across versions.
     * Pre-1.13: use STAINED_GLASS_PANE with data 7.
     * Post-1.13: use GRAY_STAINED_GLASS_PANE constant.
     */
    private static ItemStack createGrayGlass() {
        try {
            // try valueOf in case constant exists
            Material m = Material.valueOf("GRAY_STAINED_GLASS_PANE");
            return new ItemStack(m);
        } catch (IllegalArgumentException ex) {
            // older versions
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        }
    }
}
