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
            (ctx, b) -> {
                Bukkit.getOnlinePlayers().forEach(p -> b.suggest(p.getName()));
                return b.buildFuture();
            };

    private TeleportCommand() {}

    // ====================== STAFF ======================

    public static LiteralArgumentBuilder<CommandSourceStack> staff() {
        return literal("tp")
                .requires(s -> s.getSender().hasPermission("bloomvale.tp.tp"))
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(
                                    ctx.getArgument("player", String.class)
                            );
                            if (target == null) {
                                sender.sendMessage(
                                        MessageService.get("teleport.player-not-found")
                                );
                                return 0;
                            }

                            int result = TpService.tp(sender, target);
                            if (result > 0) {
                                sender.sendMessage(
                                        MessageService.get(
                                                "teleport.staff.tp.self",
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

                                    Player from = Bukkit.getPlayerExact(
                                            ctx.getArgument("player", String.class)
                                    );
                                    Player to = Bukkit.getPlayerExact(
                                            ctx.getArgument("target", String.class)
                                    );

                                    if (from == null || to == null) {
                                        sender.sendMessage(
                                                MessageService.get("teleport.player-not-found")
                                        );
                                        return 0;
                                    }

                                    int result = TpService.tp(sender, from, to);
                                    if (result > 0) {
                                        sender.sendMessage(
                                                MessageService.get(
                                                        "teleport.staff.tp.other",
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
                        sender.sendMessage(
                                MessageService.get("teleport.staff.tpall")
                        );
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

                            Player target = Bukkit.getPlayerExact(
                                    ctx.getArgument("player", String.class)
                            );
                            if (target == null) {
                                sender.sendMessage(
                                        MessageService.get("teleport.player-not-found")
                                );
                                return 0;
                            }

                            int result = TpService.tpHere(sender, target);
                            if (result > 0) {
                                sender.sendMessage(
                                        MessageService.get(
                                                "teleport.staff.tphere.sender",
                                                Map.of("player", target.getName())
                                        )
                                );

                                target.sendMessage(
                                        MessageService.get(
                                                "teleport.staff.tphere.target",
                                                Map.of("player", sender.getName())
                                        )
                                );
                            }

                            return result;
                        })
                );
    }

    // ====================== PLAYER TPA ======================

    public static LiteralArgumentBuilder<CommandSourceStack> tpa() {
        return literal("tpa")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .then(argument("player", word())
                        .suggests(ONLINE_PLAYERS)
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

                            Player target = Bukkit.getPlayerExact(
                                    ctx.getArgument("player", String.class)
                            );
                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.player-not-found"));
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

                            Player target = Bukkit.getPlayerExact(
                                    ctx.getArgument("player", String.class)
                            );
                            if (target == null) {
                                sender.sendMessage(MessageService.get("teleport.player-not-found"));
                                return 0;
                            }

                            TpaService.send(sender, target, TpaService.Type.TPAHERE);

                            sender.sendMessage(
                                    MessageService.get(
                                            "teleport.tpa.here_sent",
                                            Map.of("player", target.getName())
                                    )
                            );

                            target.sendMessage(
                                    MessageService.get(
                                            "teleport.tpa.here_received",
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
                        target.sendMessage(
                                MessageService.get("teleport.tpa.none")
                        );
                        return 0;
                    }

                    Player from = Bukkit.getPlayer(req.from());
                    if (from == null) {
                        TpaService.clear(target);
                        target.sendMessage(
                                MessageService.get("teleport.tpa.none")
                        );
                        return 0;
                    }

                    if (req.type() == TpaService.Type.TPA) {
                        from.teleport(target.getLocation());
                    } else {
                        target.teleport(from.getLocation());
                    }

                    TpaService.clear(target);

                    target.sendMessage(
                            MessageService.get("teleport.tpa.accepted")
                    );

                    from.sendMessage(
                            MessageService.get(
                                    "teleport.tpa.accepted",
                                    Map.of("player", target.getName())
                            )
                    );

                    return 1;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tpaDeny() {
        return literal("tpdeny")
                .requires(s -> s.getSender().hasPermission("bloomvale.tpa.use"))
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player target)) return 0;

                    if (TpaService.get(target) == null) {
                        target.sendMessage(
                                MessageService.get("teleport.tpa.none")
                        );
                        return 0;
                    }

                    TpaService.clear(target);

                    target.sendMessage(
                            MessageService.get("teleport.tpa.denied")
                    );

                    return 1;
                });
    }
}
