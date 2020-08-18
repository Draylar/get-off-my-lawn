package draylar.goml.command;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicInteger;

public class ClaimCommand {

    private ClaimCommand() {
        // NO-OP
    }

    public static void init() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            LiteralCommandNode<ServerCommandSource> generalNode = CommandManager
                    .literal("goml")
                    .executes(ClaimCommand::general)
                    .build();

            LiteralCommandNode<ServerCommandSource> infoNode = CommandManager
                    .literal("info")
                    .executes(ClaimCommand::info)
                    .build();

            LiteralCommandNode<ServerCommandSource> worldNode = CommandManager
                    .literal("world")
                    .executes(ClaimCommand::world)
                    .build();

            LiteralCommandNode<ServerCommandSource> removeNode = CommandManager
                    .literal("remove")
                    .executes(ClaimCommand::remove)
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
                    .build();

            LiteralCommandNode<ServerCommandSource> helpNode = CommandManager
                    .literal("help")
                    .executes(ClaimCommand::help)
                    .build();


            // usage: /goml
            dispatcher.getRoot().addChild(generalNode);

            // usage: /goml [info|world|remove|help]
            generalNode.addChild(infoNode);
            generalNode.addChild(worldNode);
            generalNode.addChild(removeNode);
            generalNode.addChild(helpNode);
        });
    }

    /**
     * Sends the player general information about all claims on the server.
     *
     * @param context  context
     * @return  success flag
     */
    private static int general(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getMinecraftServer();
        ServerPlayerEntity player = context.getSource().getPlayer();
        AtomicInteger numberOfClaimsTotal = new AtomicInteger();

        server.getWorlds().forEach(world -> {
            RTreeMap<Box, ClaimInfo> worldClaims = GetOffMyLawn.CLAIM.get(world).getClaims();
            int numberOfClaimsWorld = worldClaims.size();
            numberOfClaimsTotal.addAndGet(1);

            // TODO: translatable text
           player.sendMessage(new LiteralText(new LiteralText("GOML ") +
                                    "Number of claims in " + world.getDimension().toString() + ": " + numberOfClaimsWorld), false);
        });

        // TODO: translatable text
        player.sendMessage(new LiteralText(
                        new LiteralText("GOML ") +
                                "Number of claims across all worlds: " + numberOfClaimsTotal.get()), false);
        return 1;
    }

    /**
     * Sends the player information about the claim they are standing in, if it exists.
     *
     * @param context  context
     * @return  success flag
     */
    private static int info(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().getPlayer();

        if(!world.isClient()) {
            ClaimUtils.getClaimsAt(world, player.getBlockPos()).forEach(claimedArea -> {
                // TODO: translatable text
                // TODO: UUID -> Player name
                player.sendMessage(new LiteralText(
                        new LiteralText("GOML ") +
                        "Claim owner: " + claimedArea.getValue().getOwner()), false);
            });
        }

        return 1;
    }

    /**
     * Sends the player general information about all claims in the given world.
     *
     * @param context  context
     * @return  success flag
     */
    private static int world(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().getPlayer();

        RTreeMap<Box, ClaimInfo> worldClaims = GetOffMyLawn.CLAIM.get(world).getClaims();
        int numberOfClaims = worldClaims.size();

        // TODO: translatable text
        player.sendMessage(new LiteralText(new LiteralText("GOML ") +
                        "Number of claims in " + world.getDimension().toString() + ": " + numberOfClaims), false);
        return 1;
    }

    /**
     * Removes the claim the player is currently standing in, if it exists.
     *
     * @param context  context
     * @return  success flag
     */
    private static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().getPlayer();



        if(!world.isClient()) {
            ClaimUtils.getClaimsAt(world, player.getBlockPos()).forEach(claimedArea -> {
                GetOffMyLawn.CLAIM.get(world).remove(claimedArea.getKey());

                // TODO: translatable text
                player.sendMessage(new LiteralText(
                        new LiteralText("GOML ") + "Removed claim in " + world.getDimension().toString() +
                                " with origin of " + claimedArea.getValue().getOrigin() + "."), false);
            });
        }

        return 1;
    }

    /**
     * Sends the player information on using the /goml command.
     *
     * @param context  context
     * @return  success flag
     */
    private static int help(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        // TODO: translatable text
        player.sendMessage(new LiteralText("[Get Off My Lawn Help] "), false);
        player.sendMessage(new LiteralText("-------------------------------------"), false);
        player.sendMessage(new LiteralText("/goml - prints an overview of all claims across all worlds."), false);
        player.sendMessage(new LiteralText("/goml help - prints this message."), false);
        player.sendMessage(new LiteralText("/goml info - prints information about any claims at the user's position."), false);
        player.sendMessage(new LiteralText("/goml remove - removes any claims at the user's position."), false);
        player.sendMessage(new LiteralText("/goml world - prints an overview of claims in the user's world."), false);
        player.sendMessage(new LiteralText("-------------------------------------"), false);
        player.sendMessage(new LiteralText("GitHub repository: https://github.com/Draylar/get-off-my-lawn"), false);

        return 1;
    }
}
