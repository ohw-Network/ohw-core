package net.ohw.menus;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Random;

public class NickCommand implements CommandExecutor {

    // 隨機名字池 (你可以自己增加)
    private final String[] RANDOM_NAMES = {"Steve", "Alex", "DogLovers", "ProGamer_87", "HiddenUser", "Minecrafter", "ShadowWalker"};

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        // 權限檢查
        if (!p.hasPermission("ohw.nick.premium")) {
            p.sendMessage(ChatColor.RED + "抱歉！此功能僅限 " + ChatColor.GOLD + "MVP++ " + ChatColor.RED + " 以上玩家使用。");
            return true;
        }

        // 處理內部的隨機邏輯 (點擊書本後的動作)
        if (args.length > 0 && args[0].equalsIgnoreCase("random_internal")) {
            String randomName = RANDOM_NAMES[new Random().nextInt(RANDOM_NAMES.length)];
            
            // 設定顯示名稱 (聊天室)
            p.setDisplayName(ChatColor.GRAY + randomName);
            // 設定 Tab 列表名稱
            p.setPlayerListName(ChatColor.GRAY + randomName);
            
            p.sendMessage(ChatColor.GREEN + "你的暱稱已成功更換為: " + ChatColor.YELLOW + randomName);
            p.closeInventory();
            return true;
        }

        // 開啟 Nick 書本 GUI
        openNickBook(p);
        return true;
    }

    private void openNickBook(Player p) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent title = new TextComponent("   §0§lNickname Menu\n\n");
        TextComponent info = new TextComponent("§7點擊下方按鈕獲取一個隨機名字，你將以該名字顯示在聊天室與 Tab 列表中。\n\n");
        
        TextComponent btn = new TextComponent("   §6§l[ 點擊隨機生成 ]");
        btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick random_internal"));
        btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§e點擊獲得新身分").create()));

        meta.spigot().addPage(new net.md_5.bungee.api.chat.BaseComponent[]{title, info, btn});
        meta.setTitle("Nick Menu");
        meta.setAuthor("Server");
        book.setItemMeta(meta);
        p.openBook(book);
    }
}