package org.awesome.fabricclient.client.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.Utility;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Automatically sprints for you", Category.MOVEMENT);
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

            if(!isWDown) {
                return;
            }

            if(isSDown) {
                return;
            }

            player.setSprinting(true);
        });
    }
}
