package net.ohw.menus;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        if (!CompassMenuPlugin.MENU_TITLE.equals(e.getView().getTitle())) return;
        e.setCancelled(true); // 阻止拿取或拖動

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (clicked.getType().name().equalsIgnoreCase("COMPASS")) {
                p.closeInventory();
                String name = "";
                if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                    name = clicked.getItemMeta().getDisplayName();
                }
                // 根據名稱決定動作
                // 嘗試從名字中解析伺服器關鍵字
                for (String server : CompassMenuPlugin.SERVERS) {
                    if (name.toLowerCase().contains(server)) {
                        sendToBungee(p, server);
                        return;
                    }
                }
                // fallback
                p.sendMessage(ChatColor.GREEN + "你點了羅盤，但找不到對應的伺服器。");
            }
        }
    }

    private void sendToBungee(Player p, String server) {
        // 透過插件訊息請求 BungeeCord 進行伺服器跳轉
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            p.sendPluginMessage(CompassMenuPlugin.getInstance(), "BungeeCord", b.toByteArray());
        } catch (IOException ex) {
            p.sendMessage(ChatColor.RED + "連線到 " + server + " 失敗。");
        }
    }
}
