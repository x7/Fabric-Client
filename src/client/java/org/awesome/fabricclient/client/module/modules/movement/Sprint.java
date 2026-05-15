package org.awesome.fabricclient.client.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.Utility;

public class Sprint extends Module {
    public final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Mode", "Sprint Mode", "Legit", "Legit", "Blatant"));
    public final BooleanSetting backwardsToggle = addSetting(new BooleanSetting("Backwards", "Enable sprinting backwards", false));
    public final BooleanSetting sidewardsToggle = addSetting(new BooleanSetting("Sidewards", "Enable sprinting sidewards", false));
//    public final BooleanSetting usingItemToggle = addSetting(new BooleanSetting("Using Item", "Enable sprinting while using an item", false));
    public Sprint() {
        super("Sprint", "Automatically sprints for you", Category.MOVEMENT);
        backwardsToggle.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
        sidewardsToggle.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
//        usingItemToggle.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
    }

    @Override
    public void onEnable() {
        System.out.println(PlayerUtility.getPlayer().getId());
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(!isEnabled()) {
                return;
            }

            Player player = PlayerUtility.getPlayer();

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
        });
    }
}
