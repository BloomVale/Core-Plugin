package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.casperrs.bloomVale.services.MessageService;
import dev.casperrs.bloomVale.services.ReloadService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;

import static io.papermc.paper.command.brigadier.Commands.literal;

public final class ReloadCommand {

    private ReloadCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> register(JavaPlugin plugin) {

        return literal("bloomvale")
                .then(
                        literal("reload")
                                .requires(src ->
                                        src.getSender().hasPermission("bloomvale.reload"))
                                .executes(ctx -> {

                                    ReloadService.reload(plugin);

                                    ctx.getSource().getSender().sendMessage(
                                            MessageService.get("reload.success")
                                    );
                                    return 1;
                                })
                );
    }
}
