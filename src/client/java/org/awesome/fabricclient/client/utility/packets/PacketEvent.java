package org.awesome.fabricclient.client.utility.packets;

import net.minecraft.network.protocol.Packet;

public class PacketEvent {
    private final Packet<?> packet;
    private boolean cancelled = false;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
