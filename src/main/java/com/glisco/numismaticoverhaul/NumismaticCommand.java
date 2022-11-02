package com.glisco.numismaticoverhaul;

import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NumismaticCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(literal("numismatic")
                .then(literal("balance").requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                        .then(argument("player", EntityArgument.players())
                                .then(literal("get").executes(NumismaticCommand::get))
                                .then(longSubcommand("set", "value", NumismaticCommand::set))
                                .then(longSubcommand("add", "amount", NumismaticCommand.modify(1)))
                                .then(longSubcommand("subtract", "amount", NumismaticCommand.modify(-1)))))
                .then(literal("serverworth").executes(NumismaticCommand::serverWorth)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> longSubcommand(String name, String argName, Command<CommandSourceStack> command) {
        return literal(name).then(argument(argName, LongArgumentType.longArg(0)).executes(command));
    }

    @SuppressWarnings("ConstantConditions")
    private static int serverWorth(CommandContext<CommandSourceStack> context) {
        final var playerManager = context.getSource().getServer().getPlayerList();

        var onlineUUIDs = playerManager.getPlayers().stream().map(Entity::getUUID).toList();
        var offlineUUIDs = OfflineDataLookup.savedPlayers().stream().filter(uuid -> !onlineUUIDs.contains(uuid)).toList();

        long serverWorth = 0;
        for (var onlineId : onlineUUIDs) {
            serverWorth += ModComponents.CURRENCY.get(playerManager.getPlayer(onlineId)).getValue();
        }

        for (var offlineId : offlineUUIDs) {
            serverWorth += OfflineDataLookup.get(offlineId).getCompound("ForgeCaps").getCompound("numismaticoverhaul:currency").getLong("Value");
        }

        context.getSource().sendSuccess(toChatMessage("server net worth: " + serverWorth), false);

        return (int) serverWorth;
    }

    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var value = LongArgumentType.getLong(context, "value");
        final var players = EntityArgument.getPlayers(context, "player");

        for (var player : players) {
            //noinspection deprecation
            ModComponents.CURRENCY.get(player).setValue(value);
            context.getSource().sendSuccess(toChatMessage("balance of " + player.getScoreboardName() + " set to: " + value), false);
        }

        return CurrencyConverter.asInt(value);
    }

    private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var players = EntityArgument.getPlayers(context, "player");
        long totalBalance = 0;

        for (var player : players) {
            final long balance = ModComponents.CURRENCY.get(player).getValue();
            totalBalance += balance;

            context.getSource().sendSuccess(toChatMessage("balance of " + player.getScoreboardName() + ":"), false);
        }

        return CurrencyConverter.asInt(totalBalance);
    }

    private static Command<CommandSourceStack> modify(long multiplier) {
        return context -> {
            final var amount = LongArgumentType.getLong(context, "amount");
            final var players = EntityArgument.getPlayers(context, "player");

            long lastValue = 0;

            for (var player : players) {
                final var currencyComponent = ModComponents.CURRENCY.get(player);

                currencyComponent.silentModify(amount * multiplier);
                lastValue = currencyComponent.getValue();

                context.getSource().sendSuccess(toChatMessage("balance of " + player.getScoreboardName() + " set to: " + currencyComponent.getValue()), false);
            }

            return CurrencyConverter.asInt(lastValue);
        };
    }

    public static Component toChatMessage(String text) {
        return Component.literal("numismatic").append(Component.literal(" > " + text).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD);
    }
}
