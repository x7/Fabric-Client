package org.awesome.fabricclient.client.module.modules.movement;

import net.minecraft.world.entity.LivingEntity;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

import java.lang.reflect.Field;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("No Jump Delay", "Removes vanilla cooldown when holding jump key", Category.MOVEMENT);
    }
}
