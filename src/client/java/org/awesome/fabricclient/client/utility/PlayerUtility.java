package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PlayerUtility {
    public static Player getPlayer() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        return minecraft.player;
    }

    // This sends a message from the player
    public static void sendPlayerChat(String message) {
        ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();
        if(!clientPacketListener.hasClientLoaded()) {
            return;
        }
        clientPacketListener.sendChat(message);
    }

    // This displays a message in the players chat gui
    public static void sendPlayerMessage(Component message) {
        Player player = getPlayer();
        player.sendSystemMessage(message);
    }

    public static Entity getEntityPlayerLookingAt() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        if(minecraft.crosshairPickEntity == null) {
            return null;
        }

        return minecraft.crosshairPickEntity;
    }

    public static Block getBlockPlayerLookingAt() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        if(minecraft.level == null) {
            return null;
        }

        HitResult hitResult = minecraft.hitResult;

        if (!(hitResult instanceof BlockHitResult)) {
            return null;
        }

        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();

        return minecraft.level.getBlockState(blockPos).getBlock();
    }

    public static BlockPos getBlockPosPlayerLookingAt() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        if(minecraft.level == null) {
            return null;
        }

        HitResult hitResult = minecraft.hitResult;

        if (!(hitResult instanceof BlockHitResult)) {
            return null;
        }

        BlockHitResult blockHitResult = (BlockHitResult) hitResult;

        return blockHitResult.getBlockPos();
    }

    public static double getDistanceToBlock(BlockPos blockPos) {
        Player player = getPlayer();
        if(player == null) {
            return -1;
        }

        Vec3 playerPosition = player.position();
        Vec3 blockPosition = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return playerPosition.distanceTo(blockPosition);
    }
}
