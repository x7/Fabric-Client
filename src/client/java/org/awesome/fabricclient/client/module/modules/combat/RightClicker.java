package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.block.Block;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.RangeSliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RightClicker extends Module {
    private final RangeSliderSetting rps = addSetting(new RangeSliderSetting("RPS", "Right clicks per second", 14, 16, 1, 20));
    private final List<Item> disallowedItems = Arrays.asList(Items.BOW, Items.FISHING_ROD, Items.IRON_SWORD);
    private float tickAccumulator = 0f;

    public RightClicker() {
        super("Right Clicker", "Right Clicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(!this.isEnabled()) {
                tickAccumulator = 0f;
                return;
            }

            boolean isRightClickDown = MinecraftUtility.isRightClickDown();
            if(!isRightClickDown) {
                tickAccumulator = 0f;
                return;
            }

            if(PlayerUtility.isHoldingSword()) {
                tickAccumulator = 0f;
                return;
            }

            Player player = PlayerUtility.getPlayer();
            int minRps = rps.getMinValue();
            int maxRps = rps.getMaxValue();
            int currentRps = ThreadLocalRandom.current().nextInt(minRps, maxRps + 1);
            tickAccumulator += currentRps / 20f;

            while(tickAccumulator >= 1f) {
                tickAccumulator -= 1f;
                performClick(player);
            }
        });
    }

    private void performClick(Player player) {
        ItemStack playerHandHeldItem = player.getMainHandItem();
        if(playerHandHeldItem.isEmpty()) {
            tickAccumulator = 0f;
            return;
        }

        if(isDisallowedItem(playerHandHeldItem.getItem())) {
            tickAccumulator = 0f;
            return;
        }

        if(playerHandHeldItem.getItem() instanceof BlockItem) {
            Block block = PlayerUtility.getBlockPlayerLookingAt();
            if(block == null || block.asItem() == Items.AIR) {
                return;
            }

            try {
                Minecraft minecraft = MinecraftUtility.getMinecraftClient();
                Method method = minecraft.getClass().getDeclaredMethod("startUseItem");
                method.setAccessible(true);
                method.invoke(minecraft);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return;
        }

        ServerboundUseItemPacket serverboundUseItemPacket = new ServerboundUseItemPacket(player.getUsedItemHand(), 1, player.getYRot(), player.getXRot());
        MinecraftUtility.getPacketListener().send(serverboundUseItemPacket);
    }

    private boolean isDisallowedItem(Item item) {
        if(item == null) {
            return false;
        }

        return disallowedItems.contains(item);
    }
}