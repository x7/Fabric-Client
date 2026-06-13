package org.awesome.fabricclient.client.module.modules.utility;

import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule()
@ModuleInfo(name = "No Attack Cooldown", description = "Removes the vanilla attack cooldown", category = Category.UTILITY, active = true)
public class NoAttackCooldown extends Module {
    public NoAttackCooldown() {
        super();
    }
}
