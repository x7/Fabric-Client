package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;

public class MinecraftUtility {
    public static Minecraft getMinecraftClient() {
        return Minecraft.getInstance();
    }

    public static ClientPacketListener getPacketListener() {
        return getMinecraftClient().getConnection();
    }

    public static Connection getConnection() {
        ClientPacketListener clientPacketListener = getPacketListener();
        if(!clientPacketListener.hasClientLoaded()) {
            return null;
        }

        return clientPacketListener.getConnection();
    }
}
