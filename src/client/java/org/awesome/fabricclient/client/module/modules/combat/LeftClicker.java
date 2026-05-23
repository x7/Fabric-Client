package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.RangeSliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketManager;
import org.awesome.fabricclient.client.utility.packets.PacketUtilitys;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class LeftClicker extends Module {
    private final RangeSliderSetting cps = addSetting(new RangeSliderSetting("CPS", "Clicks Per Second", 14, 16, 1, 20));
    private final BooleanSetting allowBreakingBlocks = addSetting(new BooleanSetting("Allow Breaking Blocks", "Disables clicking while breaking a block", false));
    private boolean clientTickEventInitalized = false;
    private float tickAccumulator = 0f;

    public LeftClicker() {
        super("Left Clicker", "Left Clicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if(!clientTickEventInitalized) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if(!this.isEnabled()) {
                    tickAccumulator = 0f;
                    return;
                }

                boolean isLeftClickDown = MinecraftUtility.isLeftClickDown();
                if(!isLeftClickDown) {
                    tickAccumulator = 0f;
                    return;
                }

                Player player = PlayerUtility.getPlayer();
                if(player == null) {
                    tickAccumulator = 0f;
                    return;
                }

                // Breaking block check
                Block blockPlayerLookingAt = PlayerUtility.getBlockPlayerLookingAt();
                if((blockPlayerLookingAt != null && blockPlayerLookingAt != Block.byItem(Items.AIR))) {
                    if(allowBreakingBlocks.getValue()) {
                        BlockPos blockPlayerLookingAtPos = PlayerUtility.getBlockPosPlayerLookingAt();
                        double distanceToBlock = PlayerUtility.getDistanceToBlock(blockPlayerLookingAtPos);
                        double blockInteractionRange = player.blockInteractionRange();

                        if(blockInteractionRange > distanceToBlock) {
                            tickAccumulator = 0f;
                            return;
                        }

                        return;
                    }
                }

                int minCps = cps.getMinValue();
                int maxCps = cps.getMaxValue();
                int currentCps = ThreadLocalRandom.current().nextInt(minCps, maxCps + 1);
                tickAccumulator += currentCps / 20f;

                while(tickAccumulator >= 1f) {
                    tickAccumulator -= 1f;
                    performClick(player);
                }
            });

            clientTickEventInitalized = true;
        }
    }

    private void performClick(Player player) {
        Entity entity = PlayerUtility.getEntityPlayerLookingAt();
        if(entity != null) {
            Minecraft minecraft = MinecraftUtility.getMinecraftClient();
            try {
                Method field = minecraft.getClass().getDeclaredMethod("startAttack");
                field.setAccessible(true);
                field.invoke(minecraft);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return;
        }

        player.swing(player.getUsedItemHand(), false);
        ServerboundSwingPacket serverboundSwingPacket = new ServerboundSwingPacket(player.getUsedItemHand());
        MinecraftUtility.getPacketListener().send(serverboundSwingPacket);
    }

    @Override
    public void onDisable() {
        PacketManager.removeOutgoingListener("left_clicker_breaking_block_check");
    }
}