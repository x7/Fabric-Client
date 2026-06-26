package org.awesome.fabricclient.client.mixins.movement;

import net.minecraft.world.entity.LivingEntity;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.modules.movement.NoJumpDelay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class RemoveJumpDelayMixin {
    @Shadow
    private int noJumpDelay;

    @Unique
    private Module noJumpDelayModule;

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void removeJumpDelay(CallbackInfo ci) {
        if(noJumpDelayModule == null) {
            noJumpDelayModule = ModuleManager.getInstance().getModule(NoJumpDelay.class);
        }

        if(!noJumpDelayModule.isEnabled()) {
            return;
        }

        noJumpDelay = 0;
    }
}