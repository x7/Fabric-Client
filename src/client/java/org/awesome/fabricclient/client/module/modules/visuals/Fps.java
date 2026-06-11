package org.awesome.fabricclient.client.module.modules.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule(name = "Fps", description = "Displays your FPS", category = Category.VISUALS, active = true)
public class Fps extends Module {
    public Fps() {
        super();
    }

    @Override
    public void onEnable() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("test", "test"),
                (graphics, tick) -> {
                    graphics.text(
                            Minecraft.getInstance().font,
                            "Hi test",
                            5, 5,
                            0xFFFFFFFF,
                            false
                    );
                }
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}