package dev.casperrs.bloomVale.hooks;

import dev.casperrs.bloomVale.BloomVale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final BloomVale plugin;

    public PlaceholderAPIHook(BloomVale plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bloomvale";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Casper";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("test")) {
            return "working";
        }

        return null;
    }

}
