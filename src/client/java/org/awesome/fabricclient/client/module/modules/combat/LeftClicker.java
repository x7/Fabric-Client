package org.awesome.fabricclient.client.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.RangeSliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

@RegisterModule()
@ModuleInfo(name = "Left Clicker", description = "Automatically clicks for you", category = Category.COMBAT, active = true)
public class LeftClicker extends Module {
    private final RangeSliderSetting cps = addSetting(new RangeSliderSetting("CPS", "Clicks Per Second", 14, 16, 8, 20));
    private final BooleanSetting allowBreakingBlocks = addSetting(new BooleanSetting("Allow Breaking Blocks", "Disables clicking while breaking a block", false));
    private final BooleanSetting useExhaust = addSetting(new BooleanSetting("Use Exhaust", "Simulates real exhaust when clicking", false));
    private float tickAccumulator = 0f;
    private int totalClicks = 0; // Used for Exhaust Mode
    private boolean exhausted = false; // Used for Exhaust Mode
    private static Method startAttackMethod;

    public LeftClicker() {
        super();
    }

    @Override
    public void onTickStart() {
        boolean isLeftClickDown = MinecraftUtility.isLeftClickDown();
        if (!isLeftClickDown) {
            tickAccumulator = 0f;
            return;
        }

        Player player = PlayerUtility.getPlayer();
        if(player.isUsingItem()) {
            return;
        }

        // Breaking block check
        Block blockPlayerLookingAt = PlayerUtility.getBlockPlayerLookingAt();
        if((blockPlayerLookingAt != null && blockPlayerLookingAt != Block.byItem(Items.AIR))) {
            if (allowBreakingBlocks.getValue()) {
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

        if(useExhaust.getValue()) {
            shouldExhaust();
        }

        int minCps = cps.getMinValue();
        int maxCps = cps.getMaxValue();
        int currentCps = ThreadLocalRandom.current().nextInt(minCps, maxCps + 1);
        tickAccumulator += currentCps / 20f;

        while(tickAccumulator >= 1f) {
            if(useExhaust.getValue() && exhausted) {
                float randomFloat = ThreadLocalRandom.current().nextFloat(3.5f, 5.0f);
                tickAccumulator -= randomFloat;
            } else {
                tickAccumulator -= 1f;
            }

            performClick(player);
        }
    }

    @Override
    public void onDisable() {
        tickAccumulator = 0f;
    }

    private void performClick(Player player) {
        Entity entity = PlayerUtility.getEntityPlayerLookingAt();
        if (entity != null) {
            Minecraft minecraft = MinecraftUtility.getMinecraftClient();
            if(startAttackMethod == null) {
                try {
                    startAttackMethod = minecraft.getClass().getDeclaredMethod("startAttack");
                    startAttackMethod.setAccessible(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                startAttackMethod.invoke(minecraft);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        player.swing(player.getUsedItemHand(), false);
        ServerboundSwingPacket serverboundSwingPacket = new ServerboundSwingPacket(player.getUsedItemHand());
        MinecraftUtility.getPacketListener().send(serverboundSwingPacket);
    }

    private void performButterflyClick() {

    }

    private void performJitterClick() {

    }

    private void shouldExhaust() {
        totalClicks++;

        int randomNumber = ThreadLocalRandom.current().nextInt(2500, 4000);
        if(totalClicks > randomNumber) {
            exhausted = true;

            double randomSleepTime = ThreadLocalRandom.current().nextDouble(0.15, 0.35);
            MinecraftUtility.runLater(() -> {
                exhausted = false;
                totalClicks = 0;
            }, randomSleepTime, false);
        }
    }
}