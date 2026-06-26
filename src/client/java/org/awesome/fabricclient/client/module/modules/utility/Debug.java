package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;

@RegisterModule()
@ModuleInfo(name = "Debug", description = "Print debug information about various features", category = Category.UTILITY, active = true)
public class Debug extends Module {
    public final BooleanSetting logIncomingPackets = addSetting(new BooleanSetting("Log Incoming Packets", "Logs incoming packets", false));
    public final BooleanSetting logOutgoingPackets = addSetting(new BooleanSetting("Log Outgoing Packets", "Logs outgoing packets", false));

    public Debug() {
        super();
    }

    @Override
    public void onPacketReceive(PacketEvent packetEvent) {
        if(!logIncomingPackets.getValue()) {
            return;
        }

        Component message = Component.literal("INC: " + packetEvent.getPacket().getClass().getSimpleName());
        System.out.println(message);

        MinecraftUtility.getMinecraftClient().execute(() -> {
            PlayerUtility.sendPlayerMessage(message);
        });
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(!logOutgoingPackets.getValue()) {
            return;
        }

        Component message = Component.literal("OUT: " + packetEvent.getPacket().getClass().getSimpleName());
        System.out.println(message);

        MinecraftUtility.getMinecraftClient().execute(() -> {
            PlayerUtility.sendPlayerMessage(message);
        });
    }
}