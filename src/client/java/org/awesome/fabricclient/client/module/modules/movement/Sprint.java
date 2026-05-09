package org.awesome.fabricclient.client.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.monster.breeze.Slide;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.Vec3;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.Utility;

public class Sprint extends Module {
    public final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Mode", "Sprint Mode", "Legit", "Legit", "Blatant"));
    public final BooleanSetting backwardsSlider = addSetting(new BooleanSetting("Backwards", "Enable sprinting backwards", false));
    public final BooleanSetting sidewardsSlider = addSetting(new BooleanSetting("Sidewards", "Enable sprinting sidewards", false));

    public Sprint() {
        super("Sprint", "Automatically sprints for you", Category.MOVEMENT);
        backwardsSlider.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
        sidewardsSlider.visibleWhen(() -> mode.getValue().equalsIgnoreCase("blatant"));
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
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

            System.out.println(player.getDeltaMovement());

            if(isWDown) {
                player.setSprinting(true);
                return;
            }

            if(mode.getValue().equalsIgnoreCase("legit")) {
                return;
            }

            if(!isSDown && !isADown && !isDDown) {
                return;
            }

            if((isADown || isDDown) && sidewardsSlider.getValue()) {
                player.setSprinting(true);
                return;
            }

            if(backwardsSlider.getValue()) {
                player.setSprinting(true);
            }
        });
    }
}
