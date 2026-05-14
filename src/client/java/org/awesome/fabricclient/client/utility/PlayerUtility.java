package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
public class PlayerUtility {
    public static Player getPlayer() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        return minecraft.player;
    }

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
}
