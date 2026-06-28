package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.util.concurrent.ScheduledFuture;

@RegisterModule
@ModuleInfo(name = "Auto Tool", description = "Automatically equips the best tool for breaking the block", category = Category.UTILITY, active = true)
public class AutoTool extends Module {
    private final SliderSetting delay = addSetting(new SliderSetting("Activation Delay", "Delay for how long to take before switching to the tool", 100, 0, 500));
    private ScheduledFuture<?> currentTask = null;

    public AutoTool() {
        super();
    }

    @Override
    public void onTickStart() {
        boolean isHoldingLeftClick = MinecraftUtility.isLeftClickDown();
        if(!isHoldingLeftClick) {
            MinecraftUtility.cancelTask(currentTask, true);
            return;
        }

        Block block = PlayerUtility.getBlockPlayerLookingAt();
        if(block == null) {
            MinecraftUtility.cancelTask(currentTask, true);
            return;
        }

        Player player = PlayerUtility.getPlayer();
        Inventory inventory = player.getInventory();

        int bestToolSlot = 0;
        float bestMiningSpeed = 0.0F;
        for(int i = 0; i < 9; i++) {
            SlotAccess slotAccess = inventory.getSlot(i);
            if(slotAccess == null) {
                continue;
            }

            ItemStack itemStack = slotAccess.get();
            Tool tool = itemStack.get(DataComponents.TOOL);

            if(tool == null) {
                continue;
            }

            float miningSpeed = tool.getMiningSpeed(block.defaultBlockState());
            if(miningSpeed <= 1.0) {
                continue;
            }

            if(miningSpeed < bestMiningSpeed) {
                continue;
            }

            bestToolSlot = i;
            bestMiningSpeed = miningSpeed;
        }

        if(bestMiningSpeed == 0.0F || bestMiningSpeed == 1.0F) {
            MinecraftUtility.cancelTask(currentTask, true);
            return;
        }

        int finalBestToolSlot = bestToolSlot;
        currentTask = MinecraftUtility.runLater(() -> {
            inventory.setSelectedSlot(finalBestToolSlot);
        }, delay.getValue(), true);
    }
}