package dev.casperrs.bloomVale.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.casperrs.bloomVale.services.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Map;

public final class FlySpeedCommand {

    private static final float DEFAULT_SPEED = 0.1f;

    private FlySpeedCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("flyspeed")
                .requires(source ->
                        source.getSender() instanceof Player &&
                                source.getSender().hasPermission("bloomvale.fly.flyspeed")
                )

                .then(Commands.literal("default")
                        .executes(FlySpeedCommand::executeDefault)
                )

                .then(Commands.literal("2x")
                        .executes(ctx -> executeLevel(ctx, 2))
                )

                .then(Commands.literal("3x")
                        .executes(ctx -> executeLevel(ctx, 3))
                );
    }

    private static int executeDefault(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

        sender.setFlySpeed(DEFAULT_SPEED);
        sender.sendMessage(MessageService.get("flyspeed.reset"));
        return 1;
    }

    private static int executeLevel(CommandContext<CommandSourceStack> ctx, int level) {
        if (!(ctx.getSource().getSender() instanceof Player sender)) return 0;

        if (!sender.getAllowFlight()) {
            sender.sendMessage(MessageService.get("flyspeed.no-flight"));
            return 0;
        }

        float flySpeed = switch (level) {
            case 2 -> 0.2f;
            case 3 -> 0.3f;
            default -> DEFAULT_SPEED;
        };

        sender.setFlySpeed(flySpeed);

        sender.sendMessage(
                MessageService.get(
                        "flyspeed.set",
                        Map.of(
                                "player", sender.getName(),
                                "level", level + "x"
                        )
                )
        );


        return 1;
    }
}
