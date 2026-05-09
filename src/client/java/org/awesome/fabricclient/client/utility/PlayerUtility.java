package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerUtility {
    public static Player getPlayer() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        return minecraft.player;
    }

    public static Level getPlayerWorld() {
        return getPlayer().level();
    }
}
