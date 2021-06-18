package draylar.goml.command;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimCommand {

    private ClaimCommand() {
        // NO-OP
    }

    public static void init() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                    .literal("goml")
                    .build();

            LiteralCommandNode<ServerCommandSource> generalNode = CommandManager
                    .literal("general")
                    .executes(ClaimCommand::general)
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
                    .build();

            LiteralCommandNode<ServerCommandSource> infoNode = CommandManager
                    .literal("info")
                    .executes(ClaimCommand::info)
                    .build();

            LiteralCommandNode<ServerCommandSource> worldNode = CommandManager
                    .literal("world")
                    .executes(ClaimCommand::world)
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
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


            // User Commands
            // TODO: -> UI in the future?
            LiteralCommandNode<ServerCommandSource> trustNode = CommandManager
                    .literal("trust")
                        .then(CommandManager.argument("player", EntityArgumentType.player()).executes(context -> trust(context, false)))
                    .build();

            LiteralCommandNode<ServerCommandSource> addOwnerNode = CommandManager
                    .literal("addowner")
                        .then(CommandManager.argument("player", EntityArgumentType.player()).executes(context -> trust(context, true)))
                    .build();

            LiteralCommandNode<ServerCommandSource> untrustNode = CommandManager
                    .literal("untrust")
                    .then(CommandManager.argument("player", EntityArgumentType.player()).executes(ClaimCommand::untrust))
                    .build();


            // usage: /goml
            dispatcher.getRoot().addChild(baseNode);

            // usage: /goml [info|world|remove|help]
            baseNode.addChild(generalNode);
            baseNode.addChild(infoNode);
            baseNode.addChild(worldNode);
            baseNode.addChild(removeNode);
            baseNode.addChild(helpNode);
            baseNode.addChild(trustNode);
            baseNode.addChild(untrustNode);
            baseNode.addChild(addOwnerNode);
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

        bumpChat(player);

        server.getWorlds().forEach(world -> {
            RTreeMap<ClaimBox, Claim> worldClaims = GetOffMyLawn.CLAIM.get(world).getClaims();
            int numberOfClaimsWorld = worldClaims.size();
            numberOfClaimsTotal.addAndGet(1);

            player.sendMessage(prefix(new TranslatableText("goml.number_in", world.getRegistryKey().getValue(), numberOfClaimsWorld)), false);
        });

        player.sendMessage(prefix(new TranslatableText("goml.number_all", numberOfClaimsTotal.get()).formatted(Formatting.WHITE)), false);

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
                LiteralText owners = new LiteralText("Owners: " + Arrays.toString(claimedArea.getValue().getOwners().toArray()));
                LiteralText trusted = new LiteralText("Trusted: " + Arrays.toString(claimedArea.getValue().getTrusted().toArray()));
                player.sendMessage(prefix(owners), false);
                player.sendMessage(prefix(trusted), false);
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

        RTreeMap<ClaimBox, Claim> worldClaims = GetOffMyLawn.CLAIM.get(world).getClaims();
        int numberOfClaims = worldClaims.size();

        player.sendMessage(prefix(new TranslatableText("goml.number_in", world.getRegistryKey().getValue(), numberOfClaims)), false);

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
                player.sendMessage(prefix(new TranslatableText("goml.removed_claim", world.getRegistryKey().getValue(), claimedArea.getValue().getOrigin())), false);
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
        player.sendMessage(new LiteralText("[Get Off My Lawn Help] ").formatted(Formatting.BLUE), false);
        player.sendMessage(new LiteralText("-------------------------------------").formatted(Formatting.BLUE), false);
        player.sendMessage(new LiteralText("/goml - base command."), false);
        player.sendMessage(new LiteralText("/goml general - prints the status of all claims on the server."), false);
        player.sendMessage(new LiteralText("/goml help - prints this message."), false);
        player.sendMessage(new LiteralText("/goml info - prints information about any claims at the user's position."), false);
        player.sendMessage(new LiteralText("/goml remove - removes any claims at the user's position."), false);
        player.sendMessage(new LiteralText("/goml world - prints an overview of claims in the user's world."), false);
        player.sendMessage(new LiteralText("/goml addowner - adds an owner to the current claim."), false);
        player.sendMessage(new LiteralText("/goml trust - adds a trusted member to the current claim."), false);
        player.sendMessage(new LiteralText("/goml untrust - removes a trusted member from the current claim."), false);
        player.sendMessage(new LiteralText("-------------------------------------").formatted(Formatting.BLUE), false);
        player.sendMessage(new LiteralText("GitHub repository: https://github.com/Draylar/get-off-my-lawn"), false);

        return 1;
    }

    private static int trust(CommandContext<ServerCommandSource> context, boolean owner) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity toAdd = EntityArgumentType.getPlayer(context, "player");

        // Owner/trusted tried to add them self to the claim
        if(toAdd.getUuid().equals(player.getUuid())) {
            player.sendMessage(new TranslatableText("goml.add_self"), true);
            return 1;
        }

        if(!world.isClient()) {
            ClaimUtils.getClaimsAt(world, player.getBlockPos()).forEach(claimedArea -> {
                if(claimedArea.getValue().isOwner(player)) {
                    if(owner) {
                        claimedArea.getValue().addOwner(toAdd);
                        player.sendMessage(prefix(new TranslatableText("goml.owner_added", toAdd.getDisplayName())), false);
                    } else {
                        claimedArea.getValue().trust(toAdd);
                        player.sendMessage(prefix(new TranslatableText("goml.trusted", toAdd.getDisplayName())), false);
                    }

                    GetOffMyLawn.CLAIM.sync(player.world);
                }
            });
        }

        return 1;
    }

    private static int untrust(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity toRemove = EntityArgumentType.getPlayer(context, "player");

        // Owner/trusted tried to remove themselves from the claim
        if(toRemove.getUuid().equals(player.getUuid())) {
            player.sendMessage(new TranslatableText("goml.remove_self"), true);
            return 1;
        }

        if(!world.isClient()) {
            ClaimUtils.getClaimsAt(world, player.getBlockPos()).forEach(claimedArea -> {
                if(claimedArea.getValue().isOwner(player)) {
                    claimedArea.getValue().untrust(toRemove);
                    player.sendMessage(prefix(new TranslatableText("goml.untrusted", toRemove.getDisplayName())), false);
                    GetOffMyLawn.CLAIM.sync(player.world);
                }
            });
        }

        return 1;
    }

    private static void bumpChat(ServerPlayerEntity player) {
        player.sendMessage(new LiteralText(" "), false);
    }

    private static MutableText createPrefix() {
        return new TranslatableText("goml.prefix").formatted(Formatting.BLUE);
    }

    private static MutableText prefix(MutableText text) {
        return createPrefix().append(new LiteralText(" ")).append(text).formatted(Formatting.WHITE);
    }
}
