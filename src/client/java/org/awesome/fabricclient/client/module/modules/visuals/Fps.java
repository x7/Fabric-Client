package org.awesome.fabricclient.client.module.modules.visuals;

import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule(name = "Fps", description = "Displays your FPS", category = Category.VISUALS, active = false)
public class Fps extends Module {
    public Fps() {
        super();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}