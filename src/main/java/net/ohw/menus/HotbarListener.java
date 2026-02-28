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
    public HotbarListener(CompassMenuPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;

        // 檢查是否為第 2 格 (Slot 1)
        if (p.getInventory().getHeldItemSlot() == 1) {
            if (e.getItemInHand().getType() == Material.WOOL) {
                // 數量保持 64
                ItemStack item = e.getItemInHand();
                item.setAmount(64);

                // 5秒後消失
                final Block block = e.getBlockPlaced();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (block.getType() == Material.WOOL) {
                        block.setType(Material.AIR);
                    }
                }, 20 * 5L);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null) return;

        // 點擊第 8 格頭顱
        if (p.getInventory().getHeldItemSlot() == 7 && item.getType() == Material.SKULL_ITEM) {
            if (e.getAction().name().contains("RIGHT")) {
                e.setCancelled(true);
                p.performCommand("menu"); // 這裡暫時導向你的 menu 指令，你可以改成專屬 GUI
            }
        }
    }
}