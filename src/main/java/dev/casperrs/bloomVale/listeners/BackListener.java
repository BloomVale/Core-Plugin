package dev.casperrs.bloomVale.listeners;

import dev.casperrs.bloomVale.services.BackService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import java.util.stream.Collectors;

public final class BackListener implements Listener {

    private final boolean trackTeleports;
    private final boolean trackDeaths;
    private final Set<String> ignoreCausesUpper;

    public BackListener(boolean trackTeleports, boolean trackDeaths, Set<String> ignoreCausesUpper) {
        this.trackTeleports = trackTeleports;
        this.trackDeaths = trackDeaths;
        this.ignoreCausesUpper = ignoreCausesUpper;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!trackTeleports) return;

        if (event.getCause() != null) {
            String cause = event.getCause().name().toUpperCase();
            if (ignoreCausesUpper.contains(cause)) return;
        }

        BackService.set(event.getPlayer().getUniqueId(), event.getFrom());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!trackDeaths) return;
        BackService.set(event.getEntity().getUniqueId(), event.getEntity().getLocation());
    }

    public static Set<String> normalizeIgnoreList(java.util.List<String> raw) {
        return raw.stream().map(s -> s.toUpperCase()).collect(Collectors.toSet());
    }
}
