package dev.casperrs.bloomVale.services;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BackService {

    private static final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();

    private BackService() {}

    public static void set(UUID uuid, Location loc) {
        if (loc == null) return;
        // clone to avoid later mutation
        lastLocations.put(uuid, loc.clone());
    }

    public static Location get(UUID uuid) {
        Location loc = lastLocations.get(uuid);
        return loc == null ? null : loc.clone();
    }

    public static void clear(UUID uuid) {
        lastLocations.remove(uuid);
    }
}
