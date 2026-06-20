package org.awesome.fabricclient.client.utility;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.awesome.fabricclient.client.utility.packets.PacketUtilitys;

import java.lang.reflect.Field;
import java.util.*;

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

    public static boolean isHoldingSword() {
        Player player = getPlayer();

        if(player == null) {
            return false;
        }

        Item playerHandHeldItem = player.getMainHandItem().getItem();
        Set<Item> swords = Set.of(
                Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
                Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.COPPER_SWORD,
                Items.NETHERITE_SWORD
        );

        return swords.contains(playerHandHeldItem);
    }

    public static Map<Holder<MobEffect>, MobEffectInstance> getPlayerEffects() {
        Player player = getPlayer();

        if(player == null) {
            return Collections.emptyMap();
        }

        return player.getActiveEffectsMap();
    }

    public static int spawnFakeClone() {
        Player player = PlayerUtility.getPlayer();
        Minecraft minecraft = Minecraft.getInstance();

        UUID uuid = UUID.randomUUID();
        int entityId = (int) (Math.random() * Integer.MAX_VALUE);
        GameProfile gameProfile = new GameProfile(uuid, player.getGameProfile().name());

        try {
            ClientPacketListener connection = minecraft.getConnection();
            Field field = ClientPacketListener.class.getDeclaredField("playerInfoMap");
            field.setAccessible(true);
            Map<UUID, PlayerInfo> playerInfoMap = (Map<UUID, PlayerInfo>) field.get(connection);

            PlayerInfo fakeInfo = new PlayerInfo(new ClientboundPlayerInfoUpdatePacket.Entry(
                    uuid,
                    gameProfile,
                    false,
                    0,
                    GameType.SURVIVAL,
                    null,
                    false,
                    0,
                    null
            ).profile(), false);

            playerInfoMap.put(uuid, fakeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                entityId,
                uuid,
                player.getX(),
                player.getY(),
                player.getZ(),
                player.xRotO,
                player.yRotO,
                EntityType.PLAYER,
                0,
                player.getDeltaMovement(),
                player.yHeadRot
        );
        PacketUtilitys.sendPacketToClient(addEntityPacket);

        return entityId;
    }

    public static void deleteFakeClone(int entityId) {
        Player player = PlayerUtility.getPlayer();
        Level level = player.level();
        Player player1 = (Player) level.getEntity(entityId);

        if(player1 == null) {
            System.out.println("null entity"); // impossible to be null if this gets called then I'm gay
            return;
        }

        List<UUID> playersToRemove = new ArrayList<>();
        playersToRemove.add(player1.getUUID());

        ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(playersToRemove);
        PacketUtilitys.sendPacketToClient(clientboundPlayerInfoRemovePacket);

        ClientboundRemoveEntitiesPacket clientboundRemoveEntitiesPacket = new ClientboundRemoveEntitiesPacket(player1.getId());
        PacketUtilitys.sendPacketToClient(clientboundRemoveEntitiesPacket);
    }
}