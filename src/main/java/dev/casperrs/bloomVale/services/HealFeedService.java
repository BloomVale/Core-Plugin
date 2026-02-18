package dev.casperrs.bloomVale.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HealFeedService {

    private static JavaPlugin plugin;

    private HealFeedService() {}

    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    /* ===================== COOLDOWNS ===================== */

    public static long getHealCooldownSeconds() {
        return plugin.getConfig().getLong("cooldowns.heal-seconds", 60);
    }

    public static long getFeedCooldownSeconds() {
        return plugin.getConfig().getLong("cooldowns.feed-seconds", 60);
    }

    /* ===================== ACTIONS ===================== */

    public static void heal(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
        });
    }

    public static void feed(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.setFoodLevel(20);
            player.setSaturation(20f);
        });
    }
}
