package ru.yourname.soulshop.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.yourname.soulshop.Main;
import ru.yourname.soulshop.commands.SoulShopCommand;

import java.util.*;

public class SoulEventListener implements Listener {
    private final Random random = new Random();
    private final SoulAbilityExecutor executor = new SoulAbilityExecutor();
    private static final int[] itemPrices = {0, 5, 8, 10, 6, 12, 15, 12, 14, 20};

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (victim.getKiller() == null) return;
        String w = victim.getWorld().getName().toLowerCase();
        if (w.contains("duel") || w.contains("arena") || victim.hasMetadata("in_duel")) return;
        if (random.nextInt(100)  9) return;
            int price = itemPrices[idx];
            if (getSoulCount(player) >= price) {
                removeSouls(player, price); player.getInventory().addItem(createCustomItem(idx)); player.sendMessage("§aВы успешно купили товар!");
            } else { player.sendMessage("§cНедостаточно Душ! Требуется: " + price); }
            player.closeInventory();
        }
    }
    @EventHandler
    public void onCustomItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer(); ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Integer id = item.getItemMeta().getPersistentDataContainer().get(Main.getInstance().getItemIndexKey(), PersistentDataType.INTEGER);
        if (id == null) return;
        event.setCancelled(true); executor.handleItemUse(player, item, id);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player victim) {
            executor.handlePvPDamage(damager, victim, event);
        }
    }

    public static ItemStack createSoulItem(String victimName) {
        ItemStack soul = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta m = soul.getItemMeta();
        if (m != null) {
            m.setDisplayName("§b§lДуша"); m.setLore(Arrays.asList("§7Душа игрока: §e" + victimName, "§7Используйте у Монаха на спавне."));
            m.getPersistentDataContainer().set(Main.getInstance().getSoulKey(), PersistentDataType.BYTE, (byte) 1); soul.setItemMeta(m);
        }
        return soul;
    }

    private void openShopMenu(Player player) {
        Inventory shopMenu = Bukkit.createInventory(null, 9, "§0Магазин Сфер и Свитков");
        for (int i = 1; i <= 9; i++) { shopMenu.setItem(i - 1, createCustomItem(i)); }
        player.openInventory(shopMenu);
    }

    private ItemStack createCustomItem(int id) {
        ItemStack item = new ItemStack(Material.AIR); ItemMeta meta = null;
        switch (id) {
            case 1: item = new ItemStack(Material.PAPER); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§cСвиток \"Метеор\""); break;
            case 2: item = new ItemStack(Material.PAPER); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§eСвиток \"Священный Купол\""); break;
            case 3: item = new ItemStack(Material.PAPER); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§aСвиток \"Подмена Реальности\""); break;
            case 4: item = new ItemStack(Material.PAPER); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§8Свиток \"Печать Безмолвия\""); break;
            case 5: item = new ItemStack(Material.FIREWORK_STAR); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§4Сфера \"Вампиризм\""); break;
            case 6: item = new ItemStack(Material.FIREWORK_STAR); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§6Сфера \"Берсерк\""); break;
            case 7: item = new ItemStack(Material.FIREWORK_STAR); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§9Сфера \"Магнитный Импульс\""); break;
            case 8: item = new ItemStack(Material.FIREWORK_STAR); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§cСфера \"Перегрузка\""); break;
            case 9: item = new ItemStack(Material.FIREWORK_STAR); meta = item.getItemMeta(); if (meta != null) meta.setDisplayName("§dСфера \"Хронос\""); break;
        }
        if (meta != null) {
            meta.setLore(Collections.singletonList("§7Цена: §b" + itemPrices[id] + " Душ"));
            meta.getPersistentDataContainer().set(Main.getInstance().getItemIndexKey(), PersistentDataType.INTEGER, id); item.setItemMeta(meta);
        }
        return item;
    }

    private int getSoulCount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                if (item.getItemMeta().getPersistentDataContainer().has(Main.getInstance().getSoulKey(), PersistentDataType.BYTE)) { count += item.getAmount(); }
            }
        }
        return count;
    }

    private void removeSouls(Player player, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                if (item.getItemMeta().getPersistentDataContainer().has(Main.getInstance().getSoulKey(), PersistentDataType.BYTE)) {
                    if (item.getAmount() > amount) { item.setAmount(item.getAmount() - amount); break; }
                    else { amount -= item.getAmount(); player.getInventory().remove(item); if (amount <= 0) break; }
                }
            }
        }
    }
}

class SoulAbilityExecutor {
    private final Map<UUID, Map<Integer, Long>> cooldowns = new HashMap<>();
    private final Set<UUID> activeSilence = new HashSet<>();
    private final Set<UUID> activeVampirism = new HashSet<>();
    private final Set<UUID> activeBerserk = new HashSet<>();
    private final Set<UUID> activeOverload = new HashSet<>();
    private final Map<UUID, BukkitRunnable> activeShields = new HashMap<>();

    private static class ChronosData {
        final Location loc; final double hp; ChronosData(Location loc, double hp) { this.loc = loc; this.hp = hp; }
    }

    public void handleItemUse(Player player, ItemStack item, int id) {
        UUID uuid = player.getUniqueId();
        if (id >= 5 && id <= 8 && activeSilence.contains(uuid)) { player.sendMessage("§cМагия заблокирована эффектом Печать Безмолвия!"); return; }
        if (id >= 5 && id <= 9) {
            long current = System.currentTimeMillis(); long cdTime = getCooldown(uuid, id);
            if (current < cdTime) { player.sendMessage("§cПредмет на перезарядке! Осталось: " + ((cdTime - current) / 1000) + " сек."); return; }
        }
        boolean success = runLogic(player, id);
        if (success) {
            if (id <= 4) { item.setAmount(item.getAmount() - 1); } 
            else { int[] cdMinutes = {0, 0, 0, 0, 0, 4, 5, 3, 5, 6}; setCooldown(uuid, id, cdMinutes[id] * 60L * 1000L); }
        }
    }
    private boolean runLogic(Player player, int id) {
        switch (id) {
            case 1: return castMeteor(player);
            case 2: return castHolyDome(player);
            case 3: return castRealitySwap(player);
            case 4: return castSilenceSeal(player);
            case 5:
                activeVampirism.add(player.getUniqueId());
                new BukkitRunnable() {
                    @Override public void run() { activeVampirism.remove(player.getUniqueId()); }
                }.runTaskLater(Main.getInstance(), 160L);
                return true;
            case 6:
                activeBerserk.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
                new BukkitRunnable() {
                    @Override public void run() { activeBerserk.remove(player.getUniqueId()); }
                }.runTaskLater(Main.getInstance(), 200L);
                return true;
            case 7: return castMagneticPulse(player);
            case 8:
                if (player.getHealth() <= 8.0) return false;
                player.damage(8.0); activeOverload.add(player.getUniqueId());
                new BukkitRunnable() {
                    @Override public void run() { activeOverload.remove(player.getUniqueId()); }
                }.runTaskLater(Main.getInstance(), 120L);
                return true;
            case 9:
                ChronosData data = new ChronosData(player.getLocation(), player.getHealth());
                new BukkitRunnable() {
                    @Override public void run() { if (!player.isDead()) { player.teleport(data.loc); player.setHealth(data.hp); } }
                }.runTaskLater(Main.getInstance(), 120L);
                return true;
        }
        return false;
    }

    private boolean castMeteor(Player player) {
        Block targetBlock = player.getTargetBlock(null, 40);
        Location targetLoc = targetBlock.getLocation().add(0.5, 1, 0.5);
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ < 16) {
                    targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc, 3, 0.1, 0.1, 0.1, 0.01);
                } else {
                    targetLoc.getWorld().createExplosion(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 0F, false, false);
                    for (Entity e : targetLoc.getWorld().getNearbyEntities(targetLoc, 5, 5, 5)) {
                        if (e instanceof Player t) {
                            double d = t.getLocation().distance(targetLoc);
                            if (d <= 0.7) t.setHealth(0.0); else if (d <= 5.0) t.damage(20.0 * (1.0 - (d / 5.0)), player);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
        return true;
    }

    private boolean castHolyDome(Player player) {
        Location center = player.getLocation();
        new BukkitRunnable() {
            int seconds = 0;
            @Override
            public void run() {
                if (seconds++ >= 8) { cancel(); return; }
                for (int i = 0; i < 36; i++) {
                    double angle = i * Math.PI / 18;
                    Location pLoc = center.clone().add(Math.cos(angle) * 5, 0.2, Math.sin(angle) * 5);
                    pLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pLoc, 1, 0, 0, 0, 0);
                }
                for (Entity e : center.getWorld().getNearbyEntities(center, 5, 3, 5)) {
                    if (e instanceof Player p && !p.getUniqueId().equals(player.getUniqueId())) {
                        p.setVelocity(p.getLocation().toVector().subtract(center.toVector()).normalize().multiply(1.2).setY(0.2));
                    } else if (e instanceof Arrow) e.remove();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        return true;
    }

    private boolean castRealitySwap(Player player) {
        for (Entity e : player.getNearbyEntities(20, 20, 20)) {
            if (e instanceof Player target && player.hasLineOfSight(target)) {
                new BukkitRunnable() {
                    @Override public void run() {
                        Location l1 = player.getLocation(); Location l2 = target.getLocation();
                        player.teleport(l2); target.teleport(l1);
                    }
                }.runTaskLater(Main.getInstance(), 10L);
                return true;
            }
        }
        return false;
    }

    private boolean castSilenceSeal(Player player) {
        for (Entity e : player.getNearbyEntities(15, 15, 15)) {
            if (e instanceof Player target && player.hasLineOfSight(target)) {
                UUID tuid = target.getUniqueId(); activeSilence.add(tuid);
                activeVampirism.remove(tuid); activeBerserk.remove(tuid); activeOverload.remove(tuid);
                if (activeShields.containsKey(tuid)) activeShields.remove(tuid).cancel();
                new BukkitRunnable() {
                    @Override public void run() { activeSilence.remove(tuid); }
                }.runTaskLater(Main.getInstance(), 200L);
                return true;
            }
        }
        return false;
    }

    private boolean castMagneticPulse(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            int sec = 0;
            @Override
            public void run() {
                if (sec++ >= 12) { activeShields.remove(player.getUniqueId()); cancel(); return; }
                for (Entity e : player.getNearbyEntities(6, 6, 6)) {
                    if (e instanceof Arrow || e instanceof EnderPearl) e.remove();
                }
            }
        };
        task.runTaskTimer(Main.getInstance(), 0L, 20L);
        activeShields.put(player.getUniqueId(), task);
        return true;
    }

    public void handlePvPDamage(Player damager, Player victim, EntityDamageByEntityEvent event) {
        UUID damagerId = damager.getUniqueId();
        if (activeVampirism.contains(damagerId)) {
            double heal = event.getFinalDamage() * 0.15;
            double maxHp = damager.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
            damager.setHealth(Math.min(maxHp, damager.getHealth() + heal));
        }
        if (activeBerserk.contains(victim.getUniqueId())) event.setDamage(event.getDamage() * 1.3);
        if (activeOverload.contains(damagerId)) {
            for (ItemStack armor : victim.getInventory().getArmorContents()) {
                if (armor != null && armor.hasItemMeta() && !armor.getItemMeta().isUnbreakable()) {
                    org.bukkit.inventory.meta.Damageable dMeta = (org.bukkit.inventory.meta.Damageable) armor.getItemMeta();
                    dMeta.setDamage(dMeta.getDamage() + 3); armor.setItemMeta(dMeta);
                }
            }
        }
    }

    private long getCooldown(UUID uuid, int itemId) {
        if (!cooldowns.containsKey(uuid)) return 0;
        return cooldowns.get(uuid).getOrDefault(itemId, 0L);
    }
    private void setCooldown(UUID uuid, int itemId, long durationMs) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(itemId, System.currentTimeMillis() + durationMs);
    }
}
