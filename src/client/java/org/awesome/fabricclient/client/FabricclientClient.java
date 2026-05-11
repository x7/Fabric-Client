package org.awesome.fabricclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.awesome.fabricclient.client.gui.ClickGui;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.packets.PacketManager;
import org.lwjgl.glfw.GLFW;

public class FabricclientClient implements ClientModInitializer {
    private static boolean initialized = false;
    private static final KeyMapping OPEN_CLICK_GUI = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("key.fabricclient.click_gui", GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC)
    );

    @Override
    public void onInitializeClient() {
        System.out.println("Client has started up!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(!initialized && client.getConnection() != null) {
                PacketManager.init();
                initialized = true;
            }

            while(OPEN_CLICK_GUI.consumeClick()) {
                toggleClickGui(client);
            }
        });
    }

    private static void toggleClickGui(Minecraft client) {
        if(client.player == null) {
            return;
        }

        if(client.screen instanceof ClickGui) {
            client.setScreen(null);
            return;
        }

        client.setScreen(new ClickGui());
    }
}