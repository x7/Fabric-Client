package org.awesome.fabricclient.client.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.modules.visuals.NoHurtCam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class NoHurtCamMixin {
    @Unique
    private Module noHurtCamModule;

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void noHurtCam(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
        if(noHurtCamModule == null) {
            noHurtCamModule = ModuleManager.getInstance().getModule(NoHurtCam.class);
        }

        if(!noHurtCamModule.isEnabled()) {
            return;
        }

        System.out.println("cancelled");
        ci.cancel();
    }
}
