package dev.casperrs.bloomVale.papermc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BloomValeLogFilter extends AbstractFilter {

    private static final List<String> BLOCKED = new CopyOnWriteArrayList<>();

    private BloomValeLogFilter() {}

    /* ===================== INIT ===================== */

    public static void init(JavaPlugin plugin) {
        reload(plugin);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.getRootLogger().addFilter(new BloomValeLogFilter());
    }

    public static void reload(JavaPlugin plugin) {
        BLOCKED.clear();
        BLOCKED.addAll(
                plugin.getConfig().getStringList("blocked-messages")
                        .stream()
                        .map(String::toLowerCase)
                        .toList()
        );
    }

    /* ===================== FILTER ===================== */

    @Override
    public Result filter(LogEvent event) {
        String msg = event.getMessage().getFormattedMessage().toLowerCase();

        for (String blocked : BLOCKED) {
            if (msg.contains(blocked)) {
                return Result.DENY;
            }
        }

        return Result.NEUTRAL;
    }
}
