package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.casperrs.bloomVale.services.BackService;
import dev.casperrs.bloomVale.services.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.literal;

public final class BackCommand {

    private BackCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("back")
                .requires(s -> s.getSender().hasPermission("bloomvale.back"))
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage(MessageService.get("common.only-players"));
                        return 0;
                    }

                    Location back = BackService.get(player.getUniqueId());
                    if (back == null) {
                        player.sendMessage(MessageService.get("back.none"));
                        return 0;
                    }

                    player.teleport(back);
                    player.sendMessage(MessageService.get("back.success"));
                    return 1;
                });
    }
}
