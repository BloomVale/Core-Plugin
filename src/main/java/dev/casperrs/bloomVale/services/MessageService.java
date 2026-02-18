package dev.casperrs.bloomVale.services;

import dev.casperrs.bloomVale.hooks.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public final class MessageService {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static YamlConfiguration messages;
    private static JavaPlugin plugin;

    private MessageService() {}

    /** MUST be called once */
    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        reload();
    }

    public static synchronized void reload() {
        if (plugin == null) {
            throw new IllegalStateException("MessageService used before plugin init");
        }

        plugin.getDataFolder().mkdirs();

        File file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(file);
    }

    public static Component get(String path) {
        return get(path, Map.of());
    }

    public static Component get(String path, Map<String, String> placeholders) {
        ensureLoaded();

        String raw = messages.getString(
                path,
                "<red>Missing message: " + path + "</red>"
        );

        // 🔑 REPLACE CUSTOM PLACEHOLDERS HERE
        for (var entry : placeholders.entrySet()) {
            raw = raw.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue()
            );
        }

        // THEN parse MiniMessage and apply prefix
        return Prefix.of(MINI.deserialize(raw));
    }

    private static void ensureLoaded() {
        if (messages == null) {
            reload();
        }
    }
}
