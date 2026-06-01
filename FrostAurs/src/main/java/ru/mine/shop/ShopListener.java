package ru.mine.shop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        // Проверяем, что кликнули именно в нашем магазине по его названию
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Магазин Блоков")) {
            // Отменяем стандартное действие, чтобы игрок не украл предмет из GUI
            event.setCancelled(true);

            // Проверяем, что кликнули не по пустому слоту
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Проверяем, на какой именно предмет нажал игрок
            if (clickedItem.getType() == Material.DIAMOND) {
                // ТВОЯ ЛОГИКА ПРОВЕРКИ БАЛАНСА И СНЯТИЯ ДЕНЕГ ДОЛЖНА БЫТЬ ЗДЕСЬ
                
                // Выдаем алмаз в инвентарь игрока
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                player.sendMessage(ChatColor.GREEN + "Вы успешно купили Алмаз!");
                player.closeInventory();
            }
        }
    }
}