package ru.mine.shop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;
        
        // Создаем меню на 9 слотов с названием
        Inventory shopGui = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN + "Магазин Блоков");

        // Пример товара №1: Алмаз
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta meta = diamond.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Купить Алмаз");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Цена: " + ChatColor.GOLD + "100 монет",
                    ChatColor.YELLOW + "Нажмите, чтобы купить!"
            ));
            diamond.setItemMeta(meta);
        }

        // Кладем алмаз в самый первый слот (индексация с 0)
        shopGui.setItem(0, diamond);

        // Открываем инвентарь игроку
        player.openInventory(shopGui);
        return true;
    }
}