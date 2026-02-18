package dev.casperrs.bloomVale.services;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class GamemodeService {

    public static final String OTHERS_PERMISSION = "bloomvale.gamemode.others";

    private GamemodeService() {}

    public static int setSelf(Player player, GameMode mode) {
        player.setGameMode(mode);
        return 1;
    }

    public static int setOther(Player sender, Player target, GameMode mode) {

        if (!sender.hasPermission(OTHERS_PERMISSION)) {
            return 0;
        }

        target.setGameMode(mode);
        return 1;
    }
}
