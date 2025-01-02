package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.secretlife.TaskManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LifeSeriesCommand {
    public static final String Credits_ModCreator = "Mat0u5";
    public static final String Credits_IdeaCreator = "Grian";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("lifeseries")
                .then(literal("series")
                    .executes(context -> getSeries(context.getSource()))
                )
                .then(literal("version")
                    .executes(context -> getVersion(context.getSource()))
                )
                .then(literal("credits")
                    .executes(context -> getCredits(context.getSource()))
                )
                .then(literal("reload")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> reload(context.getSource()))
                )
                .then(literal("setSeries")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("series", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(ALLOWED_SERIES_NAMES, builder))
                        .executes(context -> setSeries(
                            context.getSource(), StringArgumentType.getString(context, "series"), false)
                        )
                        .then(literal("confirm")
                            .executes(context -> setSeries(
                                context.getSource(), StringArgumentType.getString(context, "series"), true)
                            )
                        )
                    )
                )
        );
    }

    public static int setSeries(ServerCommandSource source, String setTo, boolean confirmed) {
        if (!ALLOWED_SERIES_NAMES.contains(setTo)) {
            source.sendError(Text.of("That is not a valid series!"));
            source.sendError(Text.literal("You must choose one of the following: ").append(Text.literal(String.join(", ", ALLOWED_SERIES_NAMES)).formatted(Formatting.GRAY)));
            return -1;
        }
        if (confirmed) {
            setSeriesFinal(source, setTo);
        }
        else {
            if (currentSeries.getSeries() == SeriesList.UNASSIGNED) {
                setSeriesFinal(source, setTo);
            }
            else {
                source.sendMessage(Text.of("WARNING: you already have a selected series, are you sure you want to change to a different one?"));
                source.sendMessage(Text.literal("If you are sure, use '")
                        .append(Text.literal("/lifeseries setSeries <series>").formatted(Formatting.GRAY))
                        .append(Text.literal(" confirm").formatted(Formatting.GREEN)).append(Text.of("'")));
            }
        }
        return 1;
    }

    public static void setSeriesFinal(ServerCommandSource source, String setTo) {
        currentSeries.resetAllPlayerLives();
        config.setProperty("currentSeries", setTo);
        source.sendMessage(Text.literal("Successfully changed the series to " + setTo + ".").formatted(Formatting.GREEN));
        source.sendMessage(Text.of("---------------"));
        source.sendMessage(Text.literal("--- You must restart the server to fully finish setting up ---").formatted(Formatting.GREEN));
        source.sendMessage(Text.of("---------------"));
    }

    public static int getSeries(ServerCommandSource source) {
        source.sendMessage(Text.of("Current series: "+ SeriesList.getStringNameFromSeries(currentSeries.getSeries())));
        return 1;
    }

    public static int getVersion(ServerCommandSource source) {
        source.sendMessage(Text.of("Mod version: "+ Main.MOD_VERSION));
        return 1;
    }

    public static int reload(ServerCommandSource source) {
        if (currentSeries.getSeries() == SeriesList.SECRET_LIFE) {
            TaskManager.initialize();
        }
        seriesConfig.loadProperties();
        blacklist.reloadBlacklist();
        source.sendMessage(Text.of("Reloaded."));
        return 1;
    }

    public static int getCredits(ServerCommandSource source) {
        source.sendMessage(Text.of("The Life Series was originally created by " + Credits_IdeaCreator +
                ", and this mod aims to implement every season of the Life Series. "));
        source.sendMessage(Text.of("This mod was created by " + Credits_ModCreator + "."));
        return 1;
    }
}
