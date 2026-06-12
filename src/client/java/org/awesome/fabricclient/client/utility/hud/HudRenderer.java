package org.awesome.fabricclient.client.utility.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public class HudRenderer {
    public static void initHudRenderer() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("test", "test"),
                (graphics, deltaTracker) -> {
                    Map<String, Consumer<GuiGraphicsExtractor>> graphicsList = HudRendererUtility.getHudGraphics();

                    if(graphicsList.isEmpty()) {
                        return;
                    }

                    for(Map.Entry<String, Consumer<GuiGraphicsExtractor>> stringConsumerEntry : graphicsList.entrySet()) {
                        Consumer<GuiGraphicsExtractor> consumer = stringConsumerEntry.getValue();
                        consumer.accept(graphics);
                    }
                }
            );
    }
}