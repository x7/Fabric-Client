package org.awesome.fabricclient.client.mixins;

import net.minecraft.world.entity.LivingEntity;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.modules.movement.NoJumpDelay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class RemoveJumpDelay {
    @Shadow
    private int noJumpDelay;

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void removeJumpDelay(CallbackInfo ci) {
        Module noJumpDelayModule = ModuleManager.getInstance().getModule(NoJumpDelay.class);
        if(noJumpDelayModule == null) {
            return;
        }

        if(!noJumpDelayModule.isEnabled()) {
            return;
        }

        noJumpDelay = 0;
    }
}