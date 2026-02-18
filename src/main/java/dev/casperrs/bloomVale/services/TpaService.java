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
            UUID requester,
            Type type,
            int expiryTaskId
    ) {}

    private static final Map<UUID, Request> requests = new ConcurrentHashMap<>();
    private static final Set<UUID> disabled = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    private static final long COOLDOWN_MILLIS = 10_000; // 10 seconds

    private static JavaPlugin plugin;

    private TpaService() {}

    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    // =====================================================
    // SEND REQUEST
    // =====================================================

    public static boolean send(Player requester, Player receiver, Type type) {

        long now = System.currentTimeMillis();
        UUID requesterId = requester.getUniqueId();

        // Clean expired cooldown entry
        Long expires = cooldowns.get(requesterId);
        if (expires != null) {
            if (now >= expires) {
                cooldowns.remove(requesterId);
            } else {
                long secondsLeft = (expires - now) / 1000;
                requester.sendMessage(
                        MessageService.get(
                                "teleport.tpa.cooldown",
                                Map.of("seconds", String.valueOf(secondsLeft))
                        )
                );
                return false;
            }
        }

        // Toggle check
        if (!isEnabled(receiver) && !requester.hasPermission("bloomvale.tpa.bypass")) {
            requester.sendMessage(
                    MessageService.get(
                            "teleport.tpa.blocked",
                            Map.of("player", receiver.getName())
                    )
            );
            return false;
        }

        // Prevent duplicate active request
        if (requests.containsKey(receiver.getUniqueId())) {
            requester.sendMessage(
                    MessageService.get(
                            "teleport.tpa.already-active",
                            Map.of("player", receiver.getName())
                    )
            );
            return false;
        }

        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {

            Request req = requests.remove(receiver.getUniqueId());
            if (req == null) return;

            Player requesterPlayer = Bukkit.getPlayer(req.requester());

            receiver.sendMessage(
                    MessageService.get("teleport.tpa.expired-receiver")
            );

            if (requesterPlayer != null) {
                requesterPlayer.sendMessage(
                        MessageService.get(
                                "teleport.tpa.expired-requester",
                                Map.of("player", receiver.getName())
                        )
                );
            }

            applyCooldown(req.requester());

        }, 20L * 60).getTaskId();

        requests.put(
                receiver.getUniqueId(),
                new Request(requesterId, type, taskId)
        );

        return true;
    }

    // =====================================================
    // GET REQUEST
    // =====================================================

    public static Request get(Player receiver) {
        return requests.get(receiver.getUniqueId());
    }

    // =====================================================
    // CLEAR REQUEST
    // =====================================================

    public static void clear(Player receiver) {
        Request req = requests.remove(receiver.getUniqueId());
        if (req != null) {
            Bukkit.getScheduler().cancelTask(req.expiryTaskId());
        }
    }

    // =====================================================
    // CANCEL ALL OUTGOING
    // =====================================================

    public static int cancelAllOutgoing(Player requester) {

        UUID requesterId = requester.getUniqueId();
        int removed = 0;

        Iterator<Map.Entry<UUID, Request>> iterator = requests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Request> entry = iterator.next();

            if (!entry.getValue().requester().equals(requesterId)) continue;

            Bukkit.getScheduler().cancelTask(entry.getValue().expiryTaskId());

            Player receiver = Bukkit.getPlayer(entry.getKey());
            if (receiver != null) {
                receiver.sendMessage(
                        MessageService.get(
                                "teleport.tpa.cancelled-by-sender",
                                Map.of("player", requester.getName())
                        )
                );
            }

            iterator.remove();
            removed++;
        }

        // 🔴 APPLY COOLDOWN AFTER CANCEL
        if (removed > 0) {
            applyCooldown(requesterId);
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
            return true;
        }

        disabled.add(uuid);
        return false;
    }

    public static boolean isEnabled(Player player) {
        return !disabled.contains(player.getUniqueId());
    }

    // ======================================================
    // APPLY ANTI-JYNX SPAM
    // =======================================================
    private static void applyCooldown(UUID requester) {
        cooldowns.put(requester, System.currentTimeMillis() + COOLDOWN_MILLIS);
    }

}
