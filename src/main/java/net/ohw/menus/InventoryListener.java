package net.ohw.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        // 1. 處理「羅盤選單」的點擊
        if (CompassMenuPlugin.MENU_TITLE.equals(title)) {
            e.setCancelled(true); // 阻止拿取物品
            
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            // 只要不是背景玻璃，就嘗試解析名稱來傳送
            if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                String displayName = clicked.getItemMeta().getDisplayName().toLowerCase();
                
                for (String server : CompassMenuPlugin.SERVERS) {
                    if (displayName.contains(server.toLowerCase())) {
                        p.closeInventory();
                        sendToBungee(p, server);
                        return;
                    }
                }
            }
        }

        // 2. 處理「玩家資訊」選單的點擊
        else if (CompassMenuPlugin.PROFILE_TITLE.equals(title)) {
            e.setCancelled(true); // 阻止拿取頭顱或裝飾玻璃
            if (e.getRawSlot() == 13) {
                p.sendMessage(ChatColor.YELLOW + "正在關閉個人資訊...");
                p.closeInventory();
            }
        }

        // 3. 核心保護：防止玩家在背包中移動快捷列第 2 格 (羊毛) 與第 8 格 (頭顱)
        // 這是為了防止玩家把無限羊毛存進箱子或換位置
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
            int slot = e.getSlot();
            if (slot == 1 || slot == 7) {
                // 如果是創造模式則允許移動，否則取消
                if (p.getGameMode() != org.bukkit.GameMode.CREATIVE) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private void sendToBungee(Player p, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            p.sendPluginMessage(CompassMenuPlugin.getInstance(), "BungeeCord", b.toByteArray());
            p.sendMessage(ChatColor.GOLD + "正在傳送至 " + ChatColor.WHITE + server + "...");
        } catch (IOException ex) {
            p.sendMessage(ChatColor.RED + "連線到 " + server + " 失敗。");
        }
    }
}