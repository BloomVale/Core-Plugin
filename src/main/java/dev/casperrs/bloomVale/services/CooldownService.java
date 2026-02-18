package dev.casperrs.bloomVale.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownService {

    public record Result(boolean allowed, long secondsRemaining) {}

    private static final Map<String, Map<UUID, Long>> lastUse = new ConcurrentHashMap<>();

    private CooldownService() {}

    public static Result check(String key, UUID uuid, long cooldownSeconds) {
        if (cooldownSeconds <= 0) return new Result(true, 0);

        long now = System.currentTimeMillis();
        long cooldownMs = cooldownSeconds * 1000L;

        Map<UUID, Long> perKey = lastUse.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        Long last = perKey.get(uuid);

        if (last == null) return new Result(true, 0);

        long elapsed = now - last;
        long remaining = cooldownMs - elapsed;

        if (remaining <= 0) return new Result(true, 0);
        return new Result(false, (remaining + 999) / 1000); // round up seconds
    }

    public static void markUsed(String key, UUID uuid) {
        lastUse.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(uuid, System.currentTimeMillis());
    }
}
