package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.enums.BlinkEnum;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RegisterModule(name = "Blink", description = "Teleport yourself using real lag", category = Category.UTILITY, active = true)
public class Blink extends Module {
    private final ModeSelectSetting mode = addSetting(new ModeSelectSetting("Direction", "Direction of blink", "In", "In", "Out"));
    private final SliderSetting duration = addSetting(new SliderSetting("Duration", "Duration of blink", 1, 1, 50));
    private final Map<BlinkEnum, List<Packet<?>>> delayedPackets = new LinkedHashMap<>();
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

        addPacket(BlinkEnum.OUTBOUND, packetEvent.getPacket());
        packetEvent.cancel();
    }

    @Override
    public void onPacketReceive(PacketEvent packetEvent) {
        if(!mode.getValue().equalsIgnoreCase("in")) {
            return;
        }

        addPacket(BlinkEnum.INBOUND, packetEvent.getPacket());
        packetEvent.cancel();
    }

    @Override
    public void onDisable() {
        tickCount = 0;
        secondsInitalized = 0;
        sendAllPackets();
    }

    private void addPacket(BlinkEnum type, Packet<?> packet) {
        if(type == BlinkEnum.INBOUND) {
            if(delayedPackets.get(BlinkEnum.INBOUND) == null) {
                delayedPackets.put(BlinkEnum.INBOUND, new ArrayList<>());
            }

            delayedPackets.get(BlinkEnum.INBOUND).add(packet);
            return;
        }

        if(delayedPackets.get(BlinkEnum.OUTBOUND) == null) {
            delayedPackets.put(BlinkEnum.OUTBOUND, new ArrayList<>());
        }

        delayedPackets.get(BlinkEnum.OUTBOUND).add(packet);
    }

    private void sendAllPackets() {
        ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();

        if(delayedPackets.isEmpty()) {
            return;
        }

        for(Map.Entry<BlinkEnum, List<Packet<?>>> entry : delayedPackets.entrySet()) {
            List<Packet<?>> packet = entry.getValue();
            if(entry.getKey() == BlinkEnum.INBOUND) {
                for(Packet<?> packet1 : packet) {
                    PacketManager.getIncomingChannelHandlerContext().fireChannelRead(packet1);
                }

                return;
            }

            // has to be outbound
            for(Packet<?> packet1 : packet) {
                clientPacketListener.send(packet1);
            }
        }

        delayedPackets.clear();
    }
}