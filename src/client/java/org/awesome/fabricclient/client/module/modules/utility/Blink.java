package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

import java.util.ArrayList;
import java.util.List;

@RegisterModule(name = "Blink", description = "Teleport yourself using real lag", category = Category.UTILITY, active = false)
public class Blink extends Module {
    private final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Direction", "Direction of blink", "In", "In", "Out"));
    private final SliderSetting duration = addSetting(new SliderSetting("Duration", "Duration of blink", 1, 1, 50));
    private final List<Packet<?>> delayedIncomingPackets = new ArrayList<>();
    private final List<Packet<?>> delayedOutgoingPackets = new ArrayList<>();
    private final List<Packet<?>> forbiddenPackets = new ArrayList<>();
    private boolean incomingActivated = false;
    private boolean outgoingActivated = false;
    private int tickCount = 0;
    private int secondsInitalized = 0;

    public Blink() {
        super();
    }

    @Override
    public void onTickStart() {
        Player player = PlayerUtility.getPlayer();
        if(player == null) {
            return;
        }

        String blinkDirection = mode.getValue();

        if(blinkDirection.equalsIgnoreCase("in")) {
            incomingActivated = true;
            PacketManager.addIncomingListener("blink_incoming", packetEvent -> {
                if(!incomingActivated) {
                    return;
                }

                delayedIncomingPackets.add(packetEvent.getPacket());
                packetEvent.cancel();
            });

            if(tickCount == 20) {
                if(secondsInitalized < duration.getValue()) {
                    secondsInitalized++;
                    tickCount = 0;
                    return;
                }

                incomingActivated = false;

                for(Packet<?> packet : delayedIncomingPackets) {
                    MinecraftUtility.getPacketListener().send(packet);
                }

                delayedIncomingPackets.clear();
                this.toggle();

                return;
            }
        }

        if(blinkDirection.equalsIgnoreCase("out")) {
            outgoingActivated = true;
            PacketManager.addOutgoingListener("blink_outgoing", packetEvent -> {
                if(!outgoingActivated) {
                    return;
                }

                delayedOutgoingPackets.add(packetEvent.getPacket());
                packetEvent.cancel();
            });

            if(tickCount == 20) {
                if(secondsInitalized < duration.getValue()) {
                    secondsInitalized++;
                    tickCount = 0;
                    return;
                }

                outgoingActivated = false;

                for(Packet<?> packet : delayedOutgoingPackets) {
                    MinecraftUtility.getPacketListener().send(packet);
                }

                delayedOutgoingPackets.clear();
                this.toggle();

                return;
            }

            tickCount++;
        }
    }

    @Override
    public void onDisable() {
        PacketManager.removeIncomingListener("blink_incoming");
        PacketManager.removeOutgoingListener("blink_outgoing");
        sendAllPackets();
    }

    private void sendAllPackets() {
        ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();

        if(!delayedIncomingPackets.isEmpty()) {
            for(Packet<?> packet : delayedIncomingPackets) {
                PacketManager.getIncomingChannelHandlerContext().write(packet);
            }

            delayedIncomingPackets.clear();
        }

        if(!delayedOutgoingPackets.isEmpty()) {
            for(Packet<?> packet : delayedOutgoingPackets) {
                clientPacketListener.send(packet);
            }

            delayedOutgoingPackets.clear();
        }
    }
}