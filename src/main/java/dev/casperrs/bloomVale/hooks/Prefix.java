package dev.casperrs.bloomVale.hooks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public final class Prefix {

    private static Component prefix = Component.empty();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private Prefix() {
        // utility class
    }

    public static void init(JavaPlugin plugin) {
        String raw = plugin.getConfig().getString(
                "messages.prefix",
                "<gray>[BloomVale] "
        );

        prefix = MINI_MESSAGE.deserialize(raw);
    }

    public static Component get() {
        return prefix;
    }

    public static Component of(Component message) {
        return prefix.append(message);
    }

    public static Component of(String miniMessage) {
        return prefix.append(MINI_MESSAGE.deserialize(miniMessage));
    }
}
