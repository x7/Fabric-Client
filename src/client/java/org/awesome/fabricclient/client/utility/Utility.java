package org.awesome.fabricclient.client.utility;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class Utility {
    public static boolean isKeyDown(InputConstants.Key key) {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();

        if(key == null) {
            return false;
        }

        return InputConstants.isKeyDown(minecraft.getWindow(), key.getValue());
    }
}
