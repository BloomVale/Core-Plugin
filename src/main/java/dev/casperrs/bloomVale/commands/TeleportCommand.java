package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.casperrs.bloomVale.services.MessageService;
import dev.casperrs.bloomVale.services.TpService;
import dev.casperrs.bloomVale.services.TpaService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class TeleportCommand {

    private static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS =
            (ctx, builder) -> {
                Bukkit.getOnlinePlayers().forEach(p -> builder.suggest(p.getName()));
                return builder.buildFuture();
            };

    private TeleportCommand() {}

    // =====================================================
    // STAFF COMMANDS
    // =====================================================

    public static LiteralArgumentBuilder<CommandSourceStack> staff() {
        return literal("tp")
                .requires(s -> s.getSender().hasPermission("bloomvale.tp.tp"))

                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {

                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));

                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.errors.player-not-found"));
                                return 0;
                            }

                            if (target.equals(sender)) {
                                sender.sendMessage(MessageService.get("teleport.errors.self"));
                                return 0;
                            }

                            int result = TpService.tp(sender, target);

                            if (result > 0) {
                                sender.sendMessage(
                                        MessageService.get(
                                                "teleport.tp.self",
                                                Map.of("player", target.getName())
                                        )
                                );
                            }

                            return result;
                        })

                        .then(argument("target", word())
                                .suggests(ONLINE_PLAYERS)
                                .executes(ctx -> {

                                    if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                                    Player from = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));
                                    Player to = Bukkit.getPlayerExact(ctx.getArgument("target", String.class));

                                    if (from == null || to == null) {
                                        sender.sendMessage(MessageService.get("teleport.errors.player-not-found"));
                                        return 0;
                                    }

                                    if (from.equals(to)) {
                                        sender.sendMessage(MessageService.get("teleport.errors.self"));
                                        return 0;
                                    }

                                    int result = TpService.tp(sender, from, to);

                                    if (result > 0) {
                                        sender.sendMessage(
                                                MessageService.get(
                                                        "teleport.tp.others",
                                                        Map.of(
                                                                "from", from.getName(),
                                                                "to", to.getName()
                                                        )
                                                )
                                        );
                                    }

                                    return result;
                                })
                        )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpAll() {
        return literal("tpall")
                .requires(s -> s.getSender().hasPermission("bloomvale.tp.tpall"))
                .executes(ctx -> {

                    if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                    int result = TpService.tpAll(sender);

                    if (result > 0) {
                        sender.sendMessage(MessageService.get("teleport.tpall.self"));
                    }

                    return result;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpHere() {
        return literal("tphere")
                .requires(s -> s.getSender().hasPermission("bloomvale.tp.tphere"))
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {

                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));

                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.errors.player-not-found"));
                                return 0;
                            }

                            if (target.equals(sender)) {
                                sender.sendMessage(MessageService.get("teleport.errors.self"));
                                return 0;
                            }

                            int result = TpService.tpHere(sender, target);

                            if (result > 0) {
                                sender.sendMessage(
                                        MessageService.get(
                                                "teleport.tphere.self",
                                                Map.of("player", target.getName())
                                        )
                                );

                                target.sendMessage(
                                        MessageService.get(
                                                "teleport.tphere.others",
                                                Map.of("player", sender.getName())
                                        )
                                );
                            }

                            return result;
                        })
                );
    }

    // =====================================================
    // PLAYER TPA COMMANDS
    // =====================================================

    public static LiteralArgumentBuilder<CommandSourceStack> tpa() {
        return literal("tpa")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {

                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));

                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.errors.player-not-found"));
                                return 0;
                            }

                            if (target.equals(sender)) {
                                sender.sendMessage(MessageService.get("teleport.tpa.self-error"));
                                return 0;
                            }

                            TpaService.send(sender, target, TpaService.Type.TPA);

                            sender.sendMessage(
                                    MessageService.get(
                                            "teleport.tpa.sent",
                                            Map.of("player", target.getName())
                                    )
                            );

                            target.sendMessage(
                                    MessageService.get(
                                            "teleport.tpa.received",
                                            Map.of("player", sender.getName())
                                    )
                            );

                            return 1;
                        })
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaHere() {
        return literal("tpahere")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {

                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(ctx.getArgument("player", String.class));

                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.errors.player-not-found"));
                                return 0;
                            }

                            if (target.equals(sender)) {
                                sender.sendMessage(MessageService.get("teleport.tpahere.self-error"));
                                return 0;
                            }

                            TpaService.send(sender, target, TpaService.Type.TPAHERE);

                            sender.sendMessage(
                                    MessageService.get(
                                            "teleport.tpahere.sent",
                                            Map.of("player", target.getName())
                                    )
                            );

                            target.sendMessage(
                                    MessageService.get(
                                            "teleport.tpahere.received",
                                            Map.of("player", sender.getName())
                                    )
                            );

                            return 1;
                        })
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaAccept() {
        return literal("tpaccept")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .executes(ctx -> {

                    if (!(ctx.getSource().getSender() instanceof Player target)) return 0;

                    var req = TpaService.get(target);

                    if (req == null) {
                        target.sendMessage(MessageService.get("teleport.errors.none"));
                        return 0;
                    }

                    Player sender = Bukkit.getPlayer(req.from());

                    if (sender == null) {
                        TpaService.clear(target);
                        target.sendMessage(MessageService.get("teleport.errors.none"));
                        return 0;
                    }

                    if (req.type() == TpaService.Type.TPA) {
                        sender.teleport(target.getLocation());
                    } else {
                        target.teleport(sender.getLocation());
                    }

                    TpaService.clear(target);

                    target.sendMessage(MessageService.get("teleport.tpaccept.self"));

                    sender.sendMessage(
                            MessageService.get(
                                    "teleport.tpaccept.other",
                                    Map.of("player", target.getName())
                            )
                    );

                    return 1;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaDeny() {
        return literal("tpadeny")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .executes(ctx -> {

                    if (!(ctx.getSource().getSender() instanceof Player target)) return 0;

                    var req = TpaService.get(target);

                    if (req == null) {
                        target.sendMessage(MessageService.get("teleport.errors.none"));
                        return 0;
                    }

                    Player sender = Bukkit.getPlayer(req.from());

                    TpaService.clear(target);

                    target.sendMessage(MessageService.get("teleport.tpadeny.self"));

                    if (sender != null) {
                        sender.sendMessage(
                                MessageService.get(
                                        "teleport.tpadeny.other",
                                        Map.of("player", target.getName())
                                )
                        );
                    }

                    return 1;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaCancel() {
        return literal("tpacancel")
                .requires(s -> s.getSender() instanceof Player &&
                        s.getSender().hasPermission("bloomvale.tpa.use"))
                .executes(ctx -> {

                    if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                    int cancelled = TpaService.cancelAllOutgoing(sender);

                    if (cancelled == 0) {
                        sender.sendMessage(MessageService.get("teleport.errors.none-to-cancel"));
                        return 0;
                    }

                    sender.sendMessage(
                            MessageService.get(
                                    "teleport.tpacancel.self",
                                    Map.of("amount", String.valueOf(cancelled))
                            )
                    );

                    return cancelled;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaToggle() {
        return literal("tpatoggle")
                .requires(s -> s.getSender() instanceof Player &&
                        s.getSender().hasPermission("bloomvale.tpa.use"))
                .executes(ctx -> {

                    if (!(ctx.getSource().getSender() instanceof Player player)) return 0;

                    boolean enabled = TpaService.toggle(player);

                    player.sendMessage(
                            MessageService.get(
                                    enabled ? "teleport.tpatoggle.enabled"
                                            : "teleport.tpatoggle.disabled"
                            )
                    );

                    return 1;
                });
    }
}
