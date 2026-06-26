package org.awesome.fabricclient.client.module.modules.movement;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;

@RegisterModule
@ModuleInfo(name = "Inventory Walk", description = "Walk while your inventory is open", category = Category.MOVEMENT, active = false)
public class InventoryWalk extends Module {
    public InventoryWalk() {
        super();
    }

    @Override
    public void onTickStart() {
        Player player = PlayerUtility.getPlayer();
        if(!player.hasContainerOpen()) {
            return;
        }

        AbstractContainerMenu containerMenu = player.containerMenu;
        System.out.println(containerMenu);
    }

    //    @Override
//    public void onPacketReceive(PacketEvent packetEvent) {
//        Packet<?> packet = packetEvent.getPacket();
//
////        if(!(packet instanceof))
//    }
}
