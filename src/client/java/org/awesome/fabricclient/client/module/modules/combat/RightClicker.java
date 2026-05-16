package org.awesome.fabricclient.client.module.modules.combat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import static java.awt.geom.Point2D.distance;

public class RightClicker extends Module {
    public RightClicker() {
        super("Right Clicker", "Right Clicker", Category.COMBAT);
    }

    // Use Item On - blocks
    // Use Item - others

    @Override
    public void onEnable() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(!this.isEnabled()) {
                return;
            }

            boolean isRightClickDown = MinecraftUtility.isRightClickDown();
            if(!isRightClickDown) {
                return;
            }

            Player player = PlayerUtility.getPlayer();
            ItemStack playerHandHeldItem = player.getMainHandItem();

            if(playerHandHeldItem.getItem() == Items.AIR) {
                return;
            }

            if(player.getMainHandItem().getItem() instanceof BlockItem) {
                HitResult hitResult = MinecraftUtility.getMinecraftClient().hitResult;
                if(hitResult instanceof BlockHitResult) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    Vec3 blockLocation = blockHitResult.getLocation();
                    Vec3 playerLocation = new Vec3(player.getX(), player.getY(), player.getZ());

                    double a = playerLocation.x - blockLocation.x;
                    double b = playerLocation.y - blockLocation.y;
                    double c = playerLocation.z - blockLocation.z;
                    double finalResult = Math.sqrt((a * a) + (b * b) + (c * c));

                    System.out.println(finalResult);
                    if(finalResult > player.blockInteractionRange()) {
                        return;
                    }

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
