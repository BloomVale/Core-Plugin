package dev.casperrs.bloomVale.services;

import org.bukkit.entity.Player;

public final class FlyService {

    private FlyService() {}

    public static boolean toggle(Player target) {
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        if (!newState && target.isFlying()) {
            target.setFlying(false);
        }
        return newState;
    }
}
