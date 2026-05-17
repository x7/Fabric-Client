package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LeftClicker extends Module {
    private boolean clientTickEventInitalized = false;

    public LeftClicker() {
        super("Left Clicker", "Left Clicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if(!clientTickEventInitalized) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if(!this.isEnabled()) {
                    return;
                }

                boolean isLeftClickDown = MinecraftUtility.isLeftClickDown();
                if(!isLeftClickDown) {
                    return;
                }

                Player player = PlayerUtility.getPlayer();
                if(player == null) {
                    return;
                }

                try {
                    Minecraft minecraft = MinecraftUtility.getMinecraftClient();
                    Method field = minecraft.getClass().getDeclaredMethod("startAttack");
                    field.setAccessible(true);
                    field.invoke(minecraft);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            clientTickEventInitalized = true;
        }
    }
}
