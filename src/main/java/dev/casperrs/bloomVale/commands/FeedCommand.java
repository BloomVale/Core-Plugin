package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.casperrs.bloomVale.services.CooldownService;
import dev.casperrs.bloomVale.services.HealFeedService;
import dev.casperrs.bloomVale.services.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class FeedCommand {

    private FeedCommand() {}

    private static final String CD_KEY = "feed";

    private static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS =
            (ctx, b) -> {
                Bukkit.getOnlinePlayers().forEach(p -> b.suggest(p.getName()));
                return b.buildFuture();
            };

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("feed")
                .requires(s -> s.getSender().hasPermission("bloomvale.feed"))
                .executes(FeedCommand::self)
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .requires(s -> s.getSender().hasPermission("bloomvale.feed.others"))
                        .executes(FeedCommand::other)
                );
    }

    /* ===================== EXECUTION ===================== */

    private static int self(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(
                    MessageService.get("common.only-players")
            );
            return 0;
        }

        var res = CooldownService.check(
                CD_KEY,
                player.getUniqueId(),
                HealFeedService.getFeedCooldownSeconds()
        );

        if (!res.allowed()) {
            player.sendMessage(
                    MessageService.get(
                            "feed.cooldown",
                            Map.of("seconds", String.valueOf(res.secondsRemaining()))
                    )
            );
            return 0;
        }

        HealFeedService.feed(player);
        CooldownService.markUsed(CD_KEY, player.getUniqueId());

        player.sendMessage(MessageService.get("feed.self"));
        return 1;
    }

    private static int other(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getSender() instanceof Player sender)) {
            ctx.getSource().getSender().sendMessage(
                    MessageService.get("common.only-players")
            );
            return 0;
        }

        Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));
        if (target == null) {
            sender.sendMessage(MessageService.get("common.player-not-found"));
            return 0;
        }

        var res = CooldownService.check(
                CD_KEY,
                sender.getUniqueId(),
                HealFeedService.getFeedCooldownSeconds()
        );

        if (!res.allowed()) {
            sender.sendMessage(
                    MessageService.get(
                            "feed.cooldown",
                            Map.of("seconds", String.valueOf(res.secondsRemaining()))
                    )
            );
            return 0;
        }

        HealFeedService.feed(target);
        CooldownService.markUsed(CD_KEY, sender.getUniqueId());

        sender.sendMessage(
                MessageService.get("feed.other", Map.of("player", target.getName()))
        );
        target.sendMessage(
                MessageService.get("feed.target", Map.of("player", sender.getName()))
        );

        return 1;
    }
}
