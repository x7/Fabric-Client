package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.enums.FakeLagEnum;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

import java.util.List;
import java.util.concurrent.*;

// TODO: Add static and pulse modes

@RegisterModule(name = "Fake Lag", description = "Fake Lag", category = Category.UTILITY, active = true)
public class FakeLag extends Module {
    private final SliderSetting inboundLag = addSetting(new SliderSetting("Inbound", "Inbound fake lag delay", 0, 0, 1000));
    private final SliderSetting outboundLag = addSetting(new SliderSetting("Outbound", "Outbound fake lag delay", 0, 0, 1000));
    private final List<Packet<?>> currentSendingPackets = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8); // too much? idk

    public FakeLag() {
        super();
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(outboundLag.getValue() == 0) {
            return;
        }

        delayPacket(packetEvent, FakeLagEnum.OUTBOUND);
    }

    @Override
    public void onPacketReceive(PacketEvent packetEvent) {
        if(inboundLag.getValue() == 0) {
            return;
        }

       delayPacket(packetEvent, FakeLagEnum.INBOUND);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void delayPacket(PacketEvent packetEvent, FakeLagEnum fakeLagEnum) {
        if(currentSendingPackets.contains(packetEvent.getPacket())) {
            currentSendingPackets.remove(packetEvent.getPacket());
            return;
        }

        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        Packet<?> packet = packetEvent.getPacket();
        currentSendingPackets.add(packet);
        packetEvent.cancel();

        scheduler.schedule(() -> {
            minecraft.execute(() -> {
                if(fakeLagEnum == FakeLagEnum.INBOUND) {
                    PacketManager.getIncomingChannelHandlerContext().fireChannelRead(packet);
                    return;
                }

                ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();
                clientPacketListener.send(packet);
            });
        }, (fakeLagEnum == FakeLagEnum.INBOUND ? inboundLag.getValue() : outboundLag.getValue()), TimeUnit.MILLISECONDS);
    }
}