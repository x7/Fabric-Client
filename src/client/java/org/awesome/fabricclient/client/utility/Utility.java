package org.awesome.fabricclient.client.utility;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.util.concurrent.ThreadLocalRandom;

public class Utility {
    public static boolean isKeyDown(InputConstants.Key key) {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();

        if(key == null) {
            return false;
        }

        return InputConstants.isKeyDown(minecraft.getWindow(), key.getValue());
    }

    public static int randomNumberBetween(int first, int second) {
        return ThreadLocalRandom.current().nextInt(first, second);
    }

    // Used for the chance settings in determining if the module should activate or not
    public static boolean shouldActivateChance(int min, int max, int currentChance) {
        int randomNumber = ThreadLocalRandom.current().nextInt(min, max + 1);

        if(randomNumber <= currentChance) {
            return true;
        }

        return false;
    }
}