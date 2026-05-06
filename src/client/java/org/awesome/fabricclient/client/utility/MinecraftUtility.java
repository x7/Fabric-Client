package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;

public class MinecraftUtility {
    public static Minecraft getMinecraftClient() {
        return Minecraft.getInstance();
    }
}
