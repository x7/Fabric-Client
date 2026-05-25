package org.awesome.fabricclient.client.utility.packets;

import net.minecraft.network.protocol.Packet;

public class PacketEvent {
    private final Packet<?> packet;
    private Packet<?> overridePacket;
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

    public void overridePacket(Packet<?> packet) {
        this.overridePacket = packet;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Packet<?> getOverridePacket() {
        return this.overridePacket;
    }
}