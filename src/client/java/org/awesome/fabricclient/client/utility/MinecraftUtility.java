package org.awesome.fabricclient.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MinecraftUtility {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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

    public static boolean isLeftClickDown() {
        long window = MinecraftUtility.getMinecraftClient().getWindow().handle();
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    public static boolean isRightClickDown() {
        long window = MinecraftUtility.getMinecraftClient().getWindow().handle();
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
    }

    public static void runLater(Runnable callback, double delay) {
        scheduler.schedule(callback, ((long) delay * 1000), TimeUnit.MILLISECONDS);
    }
}
