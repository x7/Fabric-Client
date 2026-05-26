package org.awesome.fabricclient.client.module.modules.movement;

import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.Setting;

public class InstantStop extends Module {
    public InstantStop() {
        super("Instant Stop", "Instantly stops moving when the keys are released", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
