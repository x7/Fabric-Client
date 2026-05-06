package org.awesome.fabricclient.client.module.modules.combat;

import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;

public class KillAura extends Module {
    public final SliderSetting range = addSetting(new SliderSetting("Range", "Attack range", 3.5, 1.0, 6.0));
    public final SliderSetting cps = addSetting(new SliderSetting("CPS", "Clicks per second", 12.0, 1.0, 20.0));
    public final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Mode", "Aura mode", "Single", "Single", "Switch", "Multi"));
    public final BooleanSetting rotations = addSetting(new BooleanSetting("Rotations", "Rotate to target", true));
    public final BooleanSetting throughWalls = addSetting(new BooleanSetting("Through Walls", "Attack through walls", false));

    public final SliderSetting switchDelay = addSetting(new SliderSetting("Switch Delay", "Ticks between target swaps", 4.0, 1.0, 20.0));
    public final SliderSetting multiTargets = addSetting(new SliderSetting("Targets", "Max targets per swing", 3.0, 1.0, 8.0));
    public final BooleanSetting multiSpread = addSetting(new BooleanSetting("Spread Damage", "Distribute hits across targets", true));

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
        switchDelay.visibleWhen(() -> mode.getValue().equals("Switch"));
        multiTargets.visibleWhen(() -> mode.getValue().equals("Multi"));
        multiSpread.visibleWhen(() -> mode.getValue().equals("Multi"));
    }
}
