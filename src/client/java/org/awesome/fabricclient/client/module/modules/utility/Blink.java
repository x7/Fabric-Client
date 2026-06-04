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
import org.awesome.fabricclient.client.utility.packets.PacketEvent;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

import java.util.ArrayList;
import java.util.List;

@RegisterModule(name = "Blink", description = "Teleport yourself using real lag", category = Category.UTILITY, active = true)
public class Blink extends Module {
    private final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Direction", "Direction of blink", "In", "In", "Out"));
    private final SliderSetting duration = addSetting(new SliderSetting("Duration", "Duration of blink", 1, 1, 50));
    private final List<Packet<?>> delayedIncomingPackets = new ArrayList<>();
    private final List<Packet<?>> delayedOutgoingPackets = new ArrayList<>();
    private int tickCount = 0;
    private int secondsInitalized = 0;

    public Blink() {
        super();
    }

    @Override
    public void onTickStart() {
        tickCount++;

        if(tickCount < 20) {
            return;
        }

        tickCount = 0;
        secondsInitalized++;

        if(secondsInitalized < duration.getValue()) {
            return;
        }

        this.toggle();
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(!mode.getValue().equalsIgnoreCase("out")) {
            return;
        }

        delayedOutgoingPackets.add(packetEvent.getPacket());
        packetEvent.cancel();
    }

    @Override
    public void onPacketReceive(PacketEvent packetEvent) {
        if(!mode.getValue().equalsIgnoreCase("in")) {
            return;
        }

        delayedIncomingPackets.add(packetEvent.getPacket());
        packetEvent.cancel();
    }

    @Override
    public void onDisable() {
        tickCount = 0;
        secondsInitalized = 0;
        sendAllPackets();
    }

    private void sendAllPackets() {
        ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();

        if(!delayedIncomingPackets.isEmpty()) {
            for(Packet<?> packet : delayedIncomingPackets) {
                PacketManager.getIncomingChannelHandlerContext().fireChannelRead(packet);
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