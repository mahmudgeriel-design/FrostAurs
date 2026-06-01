package ru.yourname.soulshop.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SoulShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }
        openMainMenu((Player) sender);
        return true;
    }

    public static void openMainMenu(Player player) {
        Inventory mainMenu = Bukkit.createInventory(null, 27, "§0Монах");

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        if (bookMeta != null) {
            bookMeta.setDisplayName("§e§lИнформация / Квесты");
            bookMeta.setLore(Collections.singletonList("§7В разработке..."));
            book.setItemMeta(bookMeta);
        }
        mainMenu.setItem(12, book);

        ItemStack echest = new ItemStack(Material.ENDER_CHEST);
        ItemMeta echestMeta = echest.getItemMeta();
        if (echestMeta != null) {
            echestMeta.setDisplayName("§b§lОбменник Душ");
            echestMeta.setLore(Collections.singletonList("§7Нажмите, чтобы открыть магазин."));
            echest.setItemMeta(echestMeta);
        }
        mainMenu.setItem(14, echest);

        player.openInventory(mainMenu);
    }
}