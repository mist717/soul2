package com.example.heartmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

/**
 * Heart Badges - lets a player wear one of 14 heart icons (7 virtues, 7 corrupted
 * counterparts) next to their name in the tab list and above their head.
 *
 * Usage:
 *   /heart <name>   e.g. /heart determination, /heart cowardice
 *   /heart clear
 */
public class HeartMod implements ModInitializer {

    public enum HeartType {
        DETERMINATION("Determination", Formatting.RED, false),
        IRRESOLUTION("Irresolution", Formatting.RED, true),

        BRAVERY("Bravery", Formatting.GOLD, false),
        COWARDICE("Cowardice", Formatting.GOLD, true),

        JUSTICE("Justice", Formatting.YELLOW, false),
        CORRUPTION("Corruption", Formatting.YELLOW, true),

        KINDNESS("Kindness", Formatting.GREEN, false),
        ANIMOSITY("Animosity", Formatting.DARK_GREEN, true),

        PATIENCE("Patience", Formatting.AQUA, false),
        AGITATION("Agitation", Formatting.DARK_AQUA, true),

        INTEGRITY("Integrity", Formatting.BLUE, false),
        DISHONESTY("Dishonesty", Formatting.DARK_BLUE, true),

        PERSEVERANCE("Perseverance", Formatting.LIGHT_PURPLE, false),
        APATHY("Apathy", Formatting.DARK_PURPLE, true);

        public final String display;
        public final Formatting color;
        public final boolean cracked;

        HeartType(String display, Formatting color, boolean cracked) {
            this.display = display;
            this.color = color;
            this.cracked = cracked;
        }

        public String commandName() {
            return name().toLowerCase();
        }

        /** Builds the little heart icon shown before the player's name. */
        public MutableText toPrefix() {
            MutableText heart = Text.literal("\u2665 ").formatted(color);
            if (cracked) {
                heart = heart.formatted(Formatting.STRIKETHROUGH);
            }
            return heart;
        }
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var root = literal("heart");

            for (HeartType type : HeartType.values()) {
                root.then(literal(type.commandName())
                        .executes(ctx -> setHeart(ctx.getSource(), type)));
            }

            root.then(literal("clear").executes(ctx -> clearHeart(ctx.getSource())));

            dispatcher.register(root);
        });
    }

    private int setHeart(ServerCommandSource source, HeartType type) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("Only players can use this command."));
            return 0;
        }

        ServerScoreboard scoreboard = source.getServer().getScoreboard();
        String teamName = teamNameFor(player);

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
        }

        team.setPrefix(type.toPrefix());
        scoreboard.addScoreHolderToTeam(player.getGameProfile().getName(), team);

        source.sendFeedback(() -> Text.literal("Heart badge set to " + type.display)
                .formatted(type.color), false);
        return 1;
    }

    private int clearHeart(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("Only players can use this command."));
            return 0;
        }

        ServerScoreboard scoreboard = source.getServer().getScoreboard();
        Team team = scoreboard.getTeam(teamNameFor(player));
        if (team != null) {
            scoreboard.removeTeam(team);
        }

        source.sendFeedback(() -> Text.literal("Heart badge cleared."), false);
        return 1;
    }

    private String teamNameFor(ServerPlayerEntity player) {
        String base = "heart_" + player.getGameProfile().getName();
        return base.length() > 40 ? base.substring(0, 40) : base;
    }
}
