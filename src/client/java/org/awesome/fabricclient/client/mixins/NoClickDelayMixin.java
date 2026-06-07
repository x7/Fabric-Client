package org.awesome.fabricclient.client.mixins;

import net.minecraft.client.Minecraft;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.modules.utility.NoClickDelay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class NoClickDelayMixin {
    @Shadow
    public int missTime;

    @Unique
    public Module noClickDelayModule;

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void noClickDelay(CallbackInfoReturnable<Boolean> cir) {
        if(noClickDelayModule == null) {
            noClickDelayModule = ModuleManager.getInstance().getModule(NoClickDelay.class);
        }

        if(!noClickDelayModule.isEnabled()) {
            return;
        }

        this.missTime = 0;
    }
}