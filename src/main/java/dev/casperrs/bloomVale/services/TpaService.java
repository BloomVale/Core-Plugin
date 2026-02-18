package dev.casperrs.bloomVale.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
    private static final Set<UUID> disabled = ConcurrentHashMap.newKeySet();

    private static JavaPlugin plugin;

    private TpaService() {}

    // MUST be called once in onEnable
    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    // =====================================================
    // SEND REQUEST
    // =====================================================

    public static void send(Player from, Player to, Type type) {

        // Toggle check (with bypass permission)
        if (!isEnabled(to) && !from.hasPermission("bloomvale.tpa.bypass")) {
            from.sendMessage(
                    MessageService.get(
                            "teleport.tpa.blocked",
                            Map.of("player", to.getName())
                    )
            );
            return;
        }

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

    // =====================================================
    // GET REQUEST
    // =====================================================

    public static Request get(Player target) {
        return requests.get(target.getUniqueId());
    }

    // =====================================================
    // CLEAR REQUEST (accept/deny/manual clear)
    // =====================================================

    public static void clear(Player target) {
        Request req = requests.remove(target.getUniqueId());
        if (req != null) {
            Bukkit.getScheduler().cancelTask(req.expiryTaskId());
        }
    }

    // =====================================================
    // CANCEL ALL OUTGOING REQUESTS
    // =====================================================

    public static int cancelAllOutgoing(Player sender) {
        UUID senderId = sender.getUniqueId();
        int removed = 0;

        Iterator<Map.Entry<UUID, Request>> iterator = requests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Request> entry = iterator.next();

            if (!entry.getValue().from().equals(senderId)) continue;

            UUID targetId = entry.getKey();

            // Cancel expiry task
            Bukkit.getScheduler().cancelTask(entry.getValue().expiryTaskId());

            Player targetPlayer = Bukkit.getPlayer(targetId);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(
                        MessageService.get(
                                "teleport.tpa.cancelled-by-sender",
                                Map.of("player", sender.getName())
                        )
                );
            }

            iterator.remove();
            removed++;
        }

        return removed;
    }

    // =====================================================
    // TOGGLE SYSTEM
    // =====================================================

    public static boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        if (disabled.contains(uuid)) {
            disabled.remove(uuid);
            return true; // now enabled
        }

        disabled.add(uuid);
        return false; // now disabled
    }

    public static boolean isEnabled(Player player) {
        return !disabled.contains(player.getUniqueId());
    }

}
