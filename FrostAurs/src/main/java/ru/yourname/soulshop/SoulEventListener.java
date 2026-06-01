package ru.yourname.soulshop.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.yourname.soulshop.Main;
import ru.yourname.soulshop.commands.SoulShopCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class SoulEventListener implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (victim.getKiller() == null) return;
        String w = victim.getWorld().getName().toLowerCase();
        if (w.contains("duel") || w.contains("arena") || victim.hasMetadata("in_duel")) return;
        if (random.nextInt(100) < 25) {
            victim.getWorld().dropItemNaturally(victim.getLocation(), createSoulItem(victim.getName()));
        }
    }

    @EventHandler
    public void onNpcClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getName() != null && entity.getName().equalsIgnoreCase("Монах")) {
            event.setCancelled(true);
            SoulShopCommand.openMainMenu(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if (title.equals("§0Монах")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 12) {
                player.closeInventory();
                player.sendMessage("§7Система квестов сейчас находится в разработке...");
            } else if (slot == 14) {
                openShopMenu(player);
            }
        }
        if (title.equals("§0Магазин Свитков")) {
            event.setCancelled(true);
            player.sendMessage("§aВы успешно открыли меню магазина!");
            player.closeInventory();
        }
    }

    public static ItemStack createSoulItem(String victimName) {
        ItemStack soul = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta m = soul.getItemMeta();
        if (m != null) {
            m.setDisplayName("§b§lДуша");
            m.setLore(Arrays.asList("§7Душа: §e" + victimName, "§7Используйте у Монаха."));
            m.getPersistentDataContainer().set(Main.getInstance().getSoulKey(), PersistentDataType.BYTE, (byte) 1);
            soul.setItemMeta(m);
        }
        return soul;
    }

    private void openShopMenu(Player player) {
        Inventory shopMenu = Bukkit.createInventory(null, 9, "§0Магазин Свитков");
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cСвиток \"Метеор\"");
            meta.setLore(Collections.singletonList("§7Цена: §b5 Душ"));
            item.setItemMeta(meta);
        }
        shopMenu.setItem(0, item);
        player.openInventory(shopMenu);
    }
}
