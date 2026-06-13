package org.awesome.fabricclient.client.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.Utility;

import java.util.Map;

@RegisterModule()
@ModuleInfo(name = "Sprint", description = "Automatically sprints for you", category = Category.MOVEMENT, active = true)
public class Sprint extends Module {
    public final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Mode", "Sprint Mode", "Legit", "Legit", "Blatant"));
    public final BooleanSetting backwardsToggle = addSetting(new BooleanSetting("Backwards", "Enable sprinting backwards", false));
    public final BooleanSetting sidewardsToggle = addSetting(new BooleanSetting("Sidewards", "Enable sprinting sidewards", false));
    public final BooleanSetting disableOnInvis = addSetting(new BooleanSetting("Disable On Invis", "Disables while invis effect is active", false));

    public Sprint() {
        super();
        backwardsToggle.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
        sidewardsToggle.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
    }

    @Override
    public void onTickStart() {
        Player player = PlayerUtility.getPlayer();

        // Cancel while invis
        if(disableOnInvis.getValue()) {
            Map<Holder<MobEffect>, MobEffectInstance> playerEffects = PlayerUtility.getPlayerEffects();
            if(playerEffects.containsKey(MobEffects.INVISIBILITY)) {
                player.setSprinting(false);
                return;
            }
        }

        if(player.isSprinting()) {
            return;
        }

        boolean isWDown = Utility.isKeyDown(InputConstants.getKey("key.keyboard.w"));
        boolean isSDown = Utility.isKeyDown(InputConstants.getKey("key.keyboard.s"));
        boolean isADown = Utility.isKeyDown(InputConstants.getKey("key.keyboard.a"));
        boolean isDDown = Utility.isKeyDown(InputConstants.getKey("key.keyboard.d"));

        if(isWDown) {
            player.setSprinting(true);
        }

        if(mode.getValue().equalsIgnoreCase("legit")) {
            return;
        }

        // Sidewards
        if((isADown || isDDown) && sidewardsToggle.getValue()) {
            player.setSprinting(true);
        }

        // Backwards
        if(isSDown && backwardsToggle.getValue()) {
            player.setSprinting(true);
        }
    }
}
