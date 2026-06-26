package org.awesome.fabricclient.client.mixins.utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.modules.utility.NoAttackCooldown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class NoAttackCooldownMixin {
    @Unique
    private Module noAttackCoolDownModule;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void removeAttackCoolDown(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if(noAttackCoolDownModule == null) {
            noAttackCoolDownModule = ModuleManager.getInstance().getModule(NoAttackCooldown.class);
        }

        if(!noAttackCoolDownModule.isEnabled()) {
            return;
        }

        ((Entity) (Object) this).invulnerableTime = 0;
    }
}