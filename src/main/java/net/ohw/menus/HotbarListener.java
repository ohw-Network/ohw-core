package net.ohw.menus;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarListener implements Listener {
    private final CompassMenuPlugin plugin;

    public HotbarListener(CompassMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
public void onPlace(BlockPlaceEvent e) {
    Player p = e.getPlayer();
    if (p.getGameMode() == GameMode.CREATIVE) return;

    Block b = e.getBlockPlaced();
    int x = b.getX();
    int y = b.getY();
    int z = b.getZ();

    // --- 1. 重生點 3x3 保護 (中心點 8, 48, 7) ---
    int spawnX = 8;
    int spawnZ = -0; 
    if (Math.abs(x - spawnX) <= 1 && Math.abs(z - spawnZ) <= 1) {
        p.sendMessage(org.bukkit.ChatColor.RED + "你不能在重生點附近放置方塊！");
        e.setCancelled(true);
        return;
    }

    // --- 2. NPC 區域保護 (0 48 21 到 16 48 15) ---
    // 這裡我們用 Math.min/max 來確保範圍判斷正確
    if ((x >= 0 && x <= 16) && (y == 48) && (z >= 15 && z <= 21)) {
        p.sendMessage(org.bukkit.ChatColor.RED + "此區域為 NPC 區，禁止放置方塊！");
        e.setCancelled(true);
        return;
    }

    // --- 3. 無限羊毛邏輯 ---
    if (p.getInventory().getHeldItemSlot() == 1 && e.getItemInHand().getType() == Material.WOOL) {
        // 數量保持 64
        ItemStack item = e.getItemInHand();
        item.setAmount(64);

        // 5秒後消失
        final Block placedBlock = e.getBlockPlaced();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (placedBlock.getType() == Material.WOOL) {
                placedBlock.setType(Material.AIR);
            }
        }, 20 * 5L);
    }
}

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        
        // 基本檢查：物品不能為空，且必須是右鍵行為
        if (item == null || !e.getAction().name().contains("RIGHT")) return;

        // 判斷物品類型
        if (item.getType() == Material.COMPASS) {
            // 開啟伺服器傳送門選單
            p.openInventory(CompassMenuPlugin.createCompassMenu(p));
        } 
        else if (item.getType() == Material.SKULL_ITEM) {
            // 調用主插件封裝好的「非同步抓 MySQL 資料並開啟玩家資訊 GUI」
            plugin.openProfileGui(p);
        }
    }
}