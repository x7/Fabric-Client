package org.awesome.fabricclient.client.utility.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PacketManager {
    private static final List<Consumer<PacketEvent>> incomingListeners = new ArrayList<>();
    private static final List<Consumer<PacketEvent>> outgoingListeners = new ArrayList<>();

    public static void init() {
        Channel channel = PacketUtilitys.getChannel();
        if(channel == null) {
            System.out.println("Netty channel is null, Cannot initialize packet listeners");
            return;
        }

        channel.pipeline().addBefore("packet_handler", "main_incoming_packet_listener",
                new ChannelDuplexHandler() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if(!(msg instanceof Packet<?>)) {
                            super.channelRead(ctx, msg);
                            return;
                        }

                        Packet<?> packet = (Packet<?>) msg;
                        PacketEvent packetEvent = new PacketEvent(packet);
                        incomingListeners.forEach(listener -> {
                            listener.accept(packetEvent);
                        });

                        if(packetEvent.isCancelled()) {
                            return;
                        }

                        super.channelRead(ctx, msg);
                    }
                }
        );

        channel.pipeline().addBefore("packet_handler", "main_outgoing_packet_listener",
                new ChannelDuplexHandler() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        if(!(msg instanceof Packet<?>)) {
                            super.write(ctx, msg, promise);
                            return;
                        }

                        Packet<?> packet = (Packet<?>) msg;
                        PacketEvent packetEvent = new PacketEvent(packet);
                        outgoingListeners.forEach(listener -> {
                            listener.accept(packetEvent);
                        });

                        if(packetEvent.isCancelled()) {
                            return;
                        }

                        super.write(ctx, msg, promise);
                    }
                }
        );
    }

    public static void addIncomingListener(Consumer<PacketEvent> listener) {
        incomingListeners.add(listener);
    }

    public static void addOutgoingListener(Consumer<PacketEvent> listener) {
        outgoingListeners.add(listener);
    }

    public static void removeIncomingListener(Consumer<PacketEvent> listener) {
        if(!incomingListeners.contains(listener)) {
            return;
        }

        incomingListeners.remove(listener);
    }

    public static void removeOutgoingListener(Consumer<PacketEvent> listener) {
        if(!outgoingListeners.contains(listener)) {
            return;
        }

        outgoingListeners.remove(listener);
    }
}