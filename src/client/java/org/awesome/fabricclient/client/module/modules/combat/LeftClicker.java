package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.RangeSliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

public class LeftClicker extends Module {
    private final RangeSliderSetting cps = addSetting(new RangeSliderSetting("CPS", "Clicks Per Second", 14, 16, 1, 20));
    private final BooleanSetting allowBreakingBlocks = addSetting(new BooleanSetting("Allow Breaking Blocks", "Disables clicking while breaking a block", false));
    private boolean clientTickEventInitalized = false;
    private int tickCount = 0;
    private int randomCps = getRandomCps();
    private int cpsAmount = 0;

    public LeftClicker() {
        super("Left Clicker", "Left Clicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if(!clientTickEventInitalized) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                tickCount++;

                if(tickCount == 20) {
                    randomCps = getRandomCps();
                    cpsAmount = 0;
                    tickCount = 0;
                }

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

                Block blockPlayerLookingAt = PlayerUtility.getBlockPlayerLookingAt();
                if(allowBreakingBlocks.getValue() && (blockPlayerLookingAt != null && blockPlayerLookingAt != Block.byItem(Items.AIR))) {
                    BlockPos blockPlayerLookingAtPos = PlayerUtility.getBlockPosPlayerLookingAt();
                    double distanceToBlock = PlayerUtility.getDistanceToBlock(blockPlayerLookingAtPos);
                    double blockInteractionRange = player.blockInteractionRange();

                    if(blockInteractionRange > distanceToBlock) {
                        return;
                    }
                }

                if(cpsAmount >= randomCps) {
                    return;
                }

                Entity entity = PlayerUtility.getEntityPlayerLookingAt();
                if(entity != null) {
                    try {
                        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
                        Method field = minecraft.getClass().getDeclaredMethod("startAttack");
                        field.setAccessible(true);
                        field.invoke(minecraft);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    player.swing(player.getUsedItemHand(), false);
                    ServerboundSwingPacket serverboundSwingPacket = new ServerboundSwingPacket(player.getUsedItemHand());
                    MinecraftUtility.getPacketListener().send(serverboundSwingPacket);
                }

                cpsAmount++;
            });

            clientTickEventInitalized = true;
        }
    }

    private int getRandomCps() {
        int minCps = cps.getMinValue();
        int maxCps = cps.getMaxValue();

        return ThreadLocalRandom.current().nextInt(minCps, maxCps);
    }
}
