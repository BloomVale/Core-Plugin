package dev.casperrs.bloomVale;

import dev.casperrs.bloomVale.commands.*;
import dev.casperrs.bloomVale.hooks.PlaceholderAPIHook;
import dev.casperrs.bloomVale.hooks.Prefix;
import dev.casperrs.bloomVale.papermc.BloomValeLogFilter;
import dev.casperrs.bloomVale.services.HealFeedService;
import dev.casperrs.bloomVale.services.MessageService;
import dev.casperrs.bloomVale.services.TpaService;
import org.bukkit.plugin.java.JavaPlugin;
import dev.casperrs.bloomVale.listeners.BackListener;

public final class BloomVale extends JavaPlugin {

    @Override
    public void onEnable() {
        new PlaceholderAPIHook(this).register();
        saveDefaultConfig();

        Prefix.init(this);
        TpaService.init(this);
        MessageService.init(this);
        HealFeedService.init(this);
        BloomValeLogFilter.init(this);

        boolean trackTeleports = getConfig().getBoolean("back.track-teleports", true);
        boolean trackDeaths = getConfig().getBoolean("back.track-deaths", true);
        var ignore = dev.casperrs.bloomVale.listeners.BackListener.normalizeIgnoreList(
                getConfig().getStringList("back.ignore-causes")
        );

        this.getLifecycleManager().registerEventHandler(
                io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS, event -> {
                    event.registrar().register(ReloadCommand.register(this).build());
                }
        );

        getServer().getPluginManager().registerEvents(new BackListener(trackTeleports, trackDeaths, ignore), this);
        getLogger().info("BloomVale has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BloomVale has been disabled!");
    }
}
