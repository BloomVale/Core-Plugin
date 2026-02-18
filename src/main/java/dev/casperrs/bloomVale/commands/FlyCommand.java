package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.casperrs.bloomVale.services.FlyService;
import dev.casperrs.bloomVale.services.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class FlyCommand {

    private FlyCommand() {}

    private static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS =
            (ctx, b) -> {
                Bukkit.getOnlinePlayers().forEach(p -> b.suggest(p.getName()));
                return b.buildFuture();
            };

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("fly")
                .requires(s -> s.getSender().hasPermission("bloomvale.fly"))
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage(MessageService.get("common.only-players"));
                        return 0;
                    }

                    boolean enabled = FlyService.toggle(player);
                    player.sendMessage(MessageService.get(enabled ? "fly.self-on" : "fly.self-off"));
                    return 1;
                })
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .requires(s -> s.getSender().hasPermission("bloomvale.fly.others"))
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player sender)) {
                                ctx.getSource().getSender().sendMessage(MessageService.get("common.only-players"));
                                return 0;
                            }

                            Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));
                            if (target == null) {
                                sender.sendMessage(MessageService.get("common.player-not-found"));
                                return 0;
                            }

                            boolean enabled = FlyService.toggle(target);

                            sender.sendMessage(MessageService.get(
                                    enabled ? "fly.other-on" : "fly.other-off",
                                    Map.of("player", target.getName())
                            ));

                            target.sendMessage(MessageService.get(
                                    enabled ? "fly.target-on" : "fly.target-off",
                                    Map.of("player", sender.getName())
                            ));

                            return 1;
                        })
                );
    }
}
