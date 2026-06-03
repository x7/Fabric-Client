package org.awesome.fabricclient.client.module.modules.movement;

import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule(name = "No Jump Delay", description = "Removes vanilla cooldown when holding jump key", category = Category.MOVEMENT, active = true)
public class NoJumpDelay extends Module {
    public NoJumpDelay() {
    }
}
