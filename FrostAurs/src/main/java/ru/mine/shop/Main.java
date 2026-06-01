package ru.mine.shop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mine.shop.commands.ShopCommand;
import ru.mine.shop.listeners.ShopListener;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Регистрация команды /shop
        if (getCommand("shop") != null) {
            getCommand("shop").setExecutor(new ShopCommand());
        }
        
        // Регистрация обработчика событий (кликов в меню)
        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);

        getLogger().info("Плагин MineShop (Paper 1.16.5) успешно активирован!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин MineShop выключен.");
    }
}