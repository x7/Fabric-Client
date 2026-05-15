package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

public class RightClicker extends Module {
    public RightClicker() {
        super("Right Clicker", "Right Clicker", Category.COMBAT);
    }

    // Use Item On - blocks
    // Use Item - others

    @Override
    public void onEnable() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            boolean isRightClickDown = MinecraftUtility.isRightClickDown();
            if(!isRightClickDown) {
                return;
            }

            Player player = PlayerUtility.getPlayer();
            ItemStack playerHandHeldItem = player.getMainHandItem();

            if(playerHandHeldItem.getItem() == Items.AIR) {
                return;
            }

            if(player.getMainHandItem().getItem() == Items.WHITE_WOOL) {
                HitResult hitResult = MinecraftUtility.getMinecraftClient().hitResult;
                if(hitResult instanceof BlockHitResult) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                    ServerboundUseItemOnPacket serverboundUseItemOnPacket = new ServerboundUseItemOnPacket(player.getUsedItemHand(), blockHitResult, 1);
                    MinecraftUtility.getPacketListener().send(serverboundUseItemOnPacket);
                }
            } else {
                ServerboundUseItemPacket serverboundUseItemPacket = new ServerboundUseItemPacket(player.getUsedItemHand(), 1, player.getYRot(), player.getXRot());
                MinecraftUtility.getPacketListener().send(serverboundUseItemPacket);
            }

            ServerboundSwingPacket serverboundSwingPacket = new ServerboundSwingPacket(player.getUsedItemHand());
            MinecraftUtility.getPacketListener().send(serverboundSwingPacket);
        });
    }
}
