package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.casperrs.bloomVale.services.GamemodeService;
import dev.casperrs.bloomVale.services.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class GamemodeCommands {

    private GamemodeCommands() {}

    /* ===================== REGISTRATION ===================== */

    public static List<LiteralArgumentBuilder<CommandSourceStack>> commands() {
        return List.of(
                gmAlias("gmc", GameMode.CREATIVE),
                gmAlias("gms", GameMode.SURVIVAL),
                gmAlias("gma", GameMode.ADVENTURE),
                gmAlias("gmsp", GameMode.SPECTATOR)
        );
    }

    /* ===================== CORE BUILDER ===================== */
    private static LiteralArgumentBuilder<CommandSourceStack> gmAlias(String command, GameMode mode) {
        return literal(command)
                .requires(src ->
                        src.getSender().hasPermission("bloomvale.gamemode." + mode.name().toLowerCase())
                )
                // /gmc   (self)
                .executes(ctx -> executeSelf(ctx, mode))
                // /gmc <player>  (other)
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> executeOther(ctx, mode))
                );
    }


    private static int executeSelf(CommandContext<CommandSourceStack> ctx, GameMode mode) {
        if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

        int result = GamemodeService.setSelf(sender, mode);
        if (result > 0) {
            sender.sendMessage(
                    MessageService.get(
                            "gamemode.self",
                            Map.of("mode", mode.name().toLowerCase())
                    )
            );
        }
        return result;
    }

    private static int executeOther(CommandContext<CommandSourceStack> ctx, GameMode mode) {
        if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

        Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));
        if (target == null) {
            sender.sendMessage(MessageService.get("gamemode.player-not-found"));
            return 0;
        }

        int result = GamemodeService.setOther(sender, target, mode);
        if (result == 0) {
            sender.sendMessage(MessageService.get("gamemode.no-permission"));
            return 0;
        }

        sender.sendMessage(
                MessageService.get(
                        "gamemode.other.sender",
                        Map.of(
                                "player", target.getName(),
                                "mode", mode.name().toLowerCase()
                        )
                )
        );

        target.sendMessage(
                MessageService.get(
                        "gamemode.other.target",
                        Map.of(
                                "player", sender.getName(),
                                "mode", mode.name().toLowerCase()
                        )
                )
        );

        return 1;
    }


    /* ===================== TAB COMPLETE ===================== */

    private static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS =
            (context, builder) -> {

                if (!context.getSource().getSender()
                        .hasPermission(GamemodeService.OTHERS_PERMISSION)) {
                    return builder.buildFuture();
                }

                Bukkit.getOnlinePlayers().forEach(player ->
                        builder.suggest(player.getName())
                );

                return builder.buildFuture();
            };
}
