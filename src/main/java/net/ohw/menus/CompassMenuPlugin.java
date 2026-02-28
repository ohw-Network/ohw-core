package net.ohw.menus;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompassMenuPlugin extends JavaPlugin implements Listener, PluginMessageListener {
    private static CompassMenuPlugin instance;
    public static final String MENU_TITLE = ChatColor.DARK_AQUA + "Compass Menu";
    public static final String PROFILE_TITLE = ChatColor.DARK_GRAY + "玩家資訊";
    public static final String[] SERVERS = {"pvp", "shop", "bridge", "smp-1"};
    
    // 儲存人數的快取
    public static Map<String, Integer> serverCount = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        
        // 註冊事件
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new HotbarListener(this), this);

        // 註冊指令
        this.getCommand("menu").setExecutor(new MenuCommand(this));
        this.getCommand("nick").setExecutor(new NickCommand());

        // 註冊 BungeeCord 通道 (發送與接收)
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        
        // 每 5 秒自動向 BungeeCord 詢問一次各服人數
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) return;
            for (String server : SERVERS) {
                updateServerCount(server);
            }
        }, 20L, 100L); // 20 ticks 延遲, 100 ticks (5秒) 執行一次
    }

    // 發送獲取人數的請求
    private void updateServerCount(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        
        // 隨機找一位在線玩家發送訊息（Bungee 要求必須透過玩家發送）
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    // 接收來自 BungeeCord 的回傳訊息
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        
        if (subchannel.equals("PlayerCount")) {
            String server = in.readUTF();
            int count = in.readInt();
            serverCount.put(server, count); // 更新快取
        }
    }

    // --- 以下為之前的 GUI 與道具發放邏輯，保持不變 ---

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        // 羊毛 (Slot 1)
        ItemStack wool = new ItemStack(Material.WOOL, 64);
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName(ChatColor.WHITE + "無限羊毛 " + ChatColor.GRAY + "(自動消失)");
        wool.setItemMeta(woolMeta);
        p.getInventory().setItem(1, wool);

        // 頭顱 (Slot 7)
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwner(p.getName());
        headMeta.setDisplayName(ChatColor.GREEN + "玩家資訊 " + ChatColor.GRAY + "(右鍵開啟)");
        head.setItemMeta(headMeta);
        p.getInventory().setItem(7, head);
    }

    public static Inventory createCompassMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, MENU_TITLE);
        ItemStack filler = createGrayGlass();
        for (int i = 0; i < 54; i++) inv.setItem(i, filler);

        // 平均分佈在中間排 (位置 20, 21, 23, 24)
        inv.setItem(20, createServerItem("pvp", Material.WOOD_SWORD, "點擊進入激戰區"));
        inv.setItem(21, createServerItem("shop", Material.DIAMOND, "購買各式道具"));
        inv.setItem(23, createServerItem("bridge", Material.SANDSTONE, "經典 Bridge 遊戲"));
        inv.setItem(24, createServerItem("smp-1", Material.GRASS, "生存一服"));

        return inv;
    }

    private static ItemStack createServerItem(String serverID, Material material, String loreText) {
        int count = serverCount.getOrDefault(serverID, 0);
        // 數量反映人數 (最小 1，最大 64)
        int displayCount = (count <= 0) ? 1 : Math.min(count, 64);
        
        ItemStack item = new ItemStack(material, displayCount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "傳送門：" + serverID.toUpperCase());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + loreText);
            lore.add("");
            lore.add(ChatColor.AQUA + "目前人數: " + ChatColor.WHITE + count);
            lore.add("");
            lore.add(ChatColor.YELLOW + "▶ 點擊開始連線");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createGrayGlass() {
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        return glass;
    }

    public static Inventory createProfileMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, PROFILE_TITLE);
        // ... (保持之前的 Profile GUI 代碼) ...
        return inv;
    }

    public static CompassMenuPlugin getInstance() { return instance; }
    @Override
    public void onDisable() { getLogger().info("CompassMenu disabled"); }
}