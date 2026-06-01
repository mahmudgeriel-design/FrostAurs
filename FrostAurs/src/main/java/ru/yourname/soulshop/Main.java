package ru.yourname.soulshop;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import ru.yourname.soulshop.commands.SoulShopCommand;
import ru.yourname.soulshop.listeners.SoulEventListener;

public class Main extends JavaPlugin {

    private static Main instance;
    private NamespacedKey soulKey;
    private NamespacedKey itemIndexKey;

    @Override
    public void onEnable() {
        instance = this;
        this.soulKey = new NamespacedKey(this, "soul_currency");
        this.itemIndexKey = new NamespacedKey(this, "shop_item_index");

        if (getCommand("soulshop") != null) {
            getCommand("soulshop").setExecutor(new SoulShopCommand());
        }

        Bukkit.getPluginManager().registerEvents(new SoulEventListener(), this);
        getLogger().info("Плагин FrostAurs успешно запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин FrostAurs выключен.");
    }

    public static Main getInstance() { return instance; }
    public NamespacedKey getSoulKey() { return soulKey; }
    public NamespacedKey getItemIndexKey() { return itemIndexKey; }
}