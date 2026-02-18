package dev.casperrs.bloomVale.services;

import dev.casperrs.bloomVale.hooks.Prefix;
import dev.casperrs.bloomVale.papermc.BloomValeLogFilter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReloadService {

    private ReloadService() {}

    public static void reload(JavaPlugin plugin) {
        plugin.reloadConfig();

        Prefix.init(plugin);
        MessageService.reload();
        BloomValeLogFilter.reload(plugin);

        plugin.getLogger().info("BloomVale reloaded successfully.");
    }
}
