package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

public class AutoClicker extends Module {
    private boolean clientTickEventInitalized = false;

    public AutoClicker() {
        super("Auto Clicker", "Auto Clicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if(!clientTickEventInitalized) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                boolean isLeftClickDown = MinecraftUtility.isLeftClickDown();
                if(!isLeftClickDown) {
                    return;
                }

                Entity entityPlayerLookingAt = PlayerUtility.getEntityPlayerLookingAt();
                if(entityPlayerLookingAt != null) {
                    ServerboundAttackPacket serverboundAttackPacket = new ServerboundAttackPacket(entityPlayerLookingAt.getId());
                    MinecraftUtility.getPacketListener().send(serverboundAttackPacket);
                }

                PlayerUtility.getPlayer().swing(PlayerUtility.getPlayer().getUsedItemHand());
            });

            clientTickEventInitalized = true;
        }
    }
}
