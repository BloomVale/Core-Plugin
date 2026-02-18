package dev.casperrs.bloomVale.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TpaService {

    public enum Type { TPA, TPAHERE }

    public record Request(
            UUID from,
            Type type,
            int expiryTaskId
    ) {}

    private static final Map<UUID, Request> requests = new ConcurrentHashMap<>();
    private static JavaPlugin plugin;

    private TpaService() {}

    // MUST be called once in onEnable
    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    public static void send(Player from, Player to, Type type) {

        // Cancel existing request if present
        clear(to);

        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Request req = requests.remove(to.getUniqueId());
            if (req == null) return;

            Player sender = Bukkit.getPlayer(req.from());

            // Notify target
            to.sendMessage(
                    MessageService.get("teleport.tpa.expired")
            );

            // Notify sender if online
            if (sender != null) {
                sender.sendMessage(
                        MessageService.get(
                                "teleport.tpa.expired",
                                Map.of("player", to.getName())
                        )
                );
            }
        }, 20L * 60).getTaskId(); // 60 seconds

        requests.put(
                to.getUniqueId(),
                new Request(from.getUniqueId(), type, taskId)
        );
    }

    public static Request get(Player target) {
        return requests.get(target.getUniqueId());
    }

    public static void clear(Player target) {
        Request req = requests.remove(target.getUniqueId());
        if (req != null) {
            Bukkit.getScheduler().cancelTask(req.expiryTaskId());
        }
    }
}
