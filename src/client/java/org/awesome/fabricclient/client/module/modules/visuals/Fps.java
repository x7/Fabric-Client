package org.awesome.fabricclient.client.module.modules.visuals;

import net.minecraft.client.Minecraft;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.hud.HudRendererUtility;

@RegisterModule(name = "Fps", description = "Displays your FPS", category = Category.VISUALS, active = true)
public class Fps extends Module {
    public Fps() {
        super();
    }

    @Override
    public void onTickStart() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        String currentFps = String.valueOf(minecraft.getFps());
        String fpsText = "FPS: " + currentFps;

        HudRendererUtility.addOrUpdateHudGraphics("fps_module", (GuiGraphicsExtractor) -> {
            GuiGraphicsExtractor.text(MinecraftUtility.getMinecraftClient().font, fpsText, 5, 5, 0xFFFFFFFF, false);
        });
    }

    @Override
    public void onDisable() {
        HudRendererUtility.removeHudGraphics("fps_module");
    }
}