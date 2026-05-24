package org.awesome.fabricclient.client.mixins;

import net.minecraft.client.Minecraft;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class NoPlaceDelayMixin {
    @Shadow
    private int rightClickDelay;
    @Unique
    private Module noPlaceDelayModule;

    @Inject(method = "startUseItem", at = @At("TAIL"))
    private void modifyRightClickDelay(CallbackInfo ci) {
        if(noPlaceDelayModule == null) {
            noPlaceDelayModule = ModuleManager.getInstance().getModule("No Place Delay");
        }

        if(!noPlaceDelayModule.isEnabled()) {
            return;
        }

        this.rightClickDelay = 0;
    }
}