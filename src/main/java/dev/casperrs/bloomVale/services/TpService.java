package dev.casperrs.bloomVale.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TpService {

    private TpService() {}

    // ===== STAFF =====

    public static int tp(Player sender, Player target) {
        sender.teleport(target.getLocation());
        return 1;
    }

    public static int tp(Player sender, Player from, Player to) {
        from.teleport(to.getLocation());
        return 1;
    }

    public static int tpAll(Player sender) {
        Bukkit.getOnlinePlayers()
                .forEach(p -> p.teleport(sender.getLocation()));
        return 1;
    }

    public static int tpHere(Player sender, Player target) {
        target.teleport(sender.getLocation());
        return 1;
    }
}
