package dev.casperrs.bloomVale.papermc;

import dev.casperrs.bloomVale.commands.*;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public final class BloomValeBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, event -> {
                    GamemodeCommands.commands().forEach(cmd -> event.registrar().register(cmd.build()));

                    event.registrar().register(TeleportCommand.staff().build());
                    event.registrar().register(TeleportCommand.tpAll().build());
                    event.registrar().register(TeleportCommand.tpHere().build());

                    event.registrar().register(TeleportCommand.tpa().build());
                    event.registrar().register(TeleportCommand.tpaHere().build());
                    event.registrar().register(TeleportCommand.tpaAccept().build());
                    event.registrar().register(TeleportCommand.tpaDeny().build());
                    event.registrar().register(TeleportCommand.tpaCancel().build());
                    event.registrar().register(TeleportCommand.tpaToggle().build());

                    event.registrar().register(BackCommand.register().build());
                    event.registrar().register(FlyCommand.register().build());
                    event.registrar().register(FlySpeedCommand.register().build());

                    event.registrar().register(HealCommand.register().build());
                    event.registrar().register(FeedCommand.register().build());
                }
        );

    }
}
