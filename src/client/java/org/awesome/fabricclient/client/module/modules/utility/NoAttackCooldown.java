package org.awesome.fabricclient.client.module.modules.utility;

import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.SliderSetting;

public class NoAttackCooldown extends Module {
    private final SliderSetting coolDownTicks = addSetting(new SliderSetting("Ticks", "Ticks", 20, 0, 20));

    public NoAttackCooldown() {
        super("No Attack Cooldown", "Removes the vanilla attack cooldown", Category.UTILITY);
    }
}
