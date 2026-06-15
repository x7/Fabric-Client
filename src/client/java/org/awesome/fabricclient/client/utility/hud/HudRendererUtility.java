package org.awesome.fabricclient.client.utility.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HudRendererUtility {
    private final static Map<String, Consumer<GuiGraphicsExtractor>> hudGraphics = new LinkedHashMap<>();

    public static Map<String, Consumer<GuiGraphicsExtractor>> getHudGraphics() {
        return hudGraphics;
    }

    public static void addOrUpdateHudGraphics(String name, Consumer<GuiGraphicsExtractor> consumer) {
        hudGraphics.put(name, consumer);
    }

    public static void removeHudGraphics(String name) {
        if(!hudGraphics.containsKey(name)) {
            return;
        }

        hudGraphics.remove(name);
    }
}