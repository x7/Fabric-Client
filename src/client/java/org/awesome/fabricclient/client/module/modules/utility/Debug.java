package org.awesome.fabricclient.client.module.modules.utility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;
import org.awesome.fabricclient.client.utility.packets.PacketManager;
import org.awesome.fabricclient.client.utility.packets.PacketUtilitys;

import java.util.function.Consumer;

public class Debug extends Module {
    public final BooleanSetting logIncomingPackets = addSetting(new BooleanSetting("Log Incoming Packets", "Logs incoming packets", false));
    public final BooleanSetting logOutgoingPackets = addSetting(new BooleanSetting("Log Outgoing Packets", "Logs outgoing packets", false));


    public Debug() {
        super("Debug", "Debug mode for various features", Category.UTILITY);
    }

    @Override
    public void onEnable() {
        // fix this
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(logIncomingPackets.getValue()) {
                PacketManager.addIncomingListener("debug_incoming", packetEvent -> {
                    Component message = Component.literal("INC: " + packetEvent.getPacket().getClass().getSimpleName());
                    MinecraftUtility.getMinecraftClient().execute(() -> {
                        PlayerUtility.sendPlayerMessage(message);
                    });
                });
            }

            if(logOutgoingPackets.getValue()) {
                PacketManager.addOutgoingListener("debug_outgoing", packetEvent -> {
                    Component message = Component.literal("OUT: " + packetEvent.getPacket().getClass().getSimpleName());
                    if(packetEvent.getPacket() instanceof ServerboundAttackPacket) {
                        System.out.println(PacketUtilitys.getAllPacketFields(packetEvent.getPacket()));
                    }

                    MinecraftUtility.getMinecraftClient().execute(() -> {
                        PlayerUtility.sendPlayerMessage(message);
                    });
                });
            }
        });
    }

    @Override
    public void onDisable() {
    }
}
