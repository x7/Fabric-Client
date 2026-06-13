package org.awesome.fabricclient.client.utility.packets;

import io.netty.channel.*;
import net.minecraft.network.protocol.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class PacketManager {
    private static final ConcurrentMap<String, Consumer<PacketEvent>> incomingListeners = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Consumer<PacketEvent>> outgoingListeners = new ConcurrentHashMap<>();
    private static ChannelPipeline incomingPipeline;
    private static ChannelPipeline outgoingPipeline;
    private static ChannelHandlerContext incomingChannelHandlerContext;
    private static ChannelHandlerContext outgoingChannelHandlerContext;

    public static void init() {
        Channel channel = PacketUtilitys.getChannel();
        if(channel == null) {
            System.out.println("Netty channel is null, Cannot initialize packet listeners");
            return;
        }

        incomingPipeline = channel.pipeline().addBefore("packet_handler", "main_incoming_packet_listener",
                new ChannelDuplexHandler() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if(incomingChannelHandlerContext == null) {
                            incomingChannelHandlerContext = ctx;
                        }

                        if(!(msg instanceof Packet<?>)) {
                            super.channelRead(ctx, msg);
                            return;
                        }

                        Packet<?> packet = (Packet<?>) msg;
                        PacketEvent packetEvent = new PacketEvent(packet);
                        incomingListeners.forEach((name, packetEventConsumer) -> {
                            packetEventConsumer.accept(packetEvent);
                        });

                        if(packetEvent.isCancelled()) {
                            return;
                        }

                        if(packetEvent.getOverridePacket() != null) {
                            super.channelRead(ctx, packetEvent.getOverridePacket());
                            return;
                        }

                        super.channelRead(ctx, msg);
                    }
                }
        );

        outgoingPipeline = channel.pipeline().addBefore("packet_handler", "main_outgoing_packet_listener",
                new ChannelDuplexHandler() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        if(!(msg instanceof Packet<?>)) {
                            super.write(ctx, msg, promise);
                            return;
                        }

                        Packet<?> packet = (Packet<?>) msg;
                        PacketEvent packetEvent = new PacketEvent(packet);
                        outgoingListeners.forEach((name, packetEventConsumer) -> {
                            packetEventConsumer.accept(packetEvent);
                        });

                        if(packetEvent.isCancelled()) {
                            return;
                        }

                        if(packetEvent.getOverridePacket() != null) {
                            super.write(ctx, packetEvent.getOverridePacket(), promise);
                            return;
                        }

                        super.write(ctx, packetEvent.getPacket(), promise);
                    }
                }
        );
    }

    public static void deleteChannelPipelines() {
        incomingPipeline.deregister();
        outgoingPipeline.deregister();

        incomingPipeline = null;
        outgoingPipeline = null;

        incomingListeners.clear();
        outgoingListeners.clear();
    }

    public static void addIncomingListener(String name, Consumer<PacketEvent> listener) {
        if(incomingListeners.containsKey(name)) {
            return;
        }

        incomingListeners.put(name, listener);
    }

    public static void addOutgoingListener(String name, Consumer<PacketEvent> listener) {
        if(outgoingListeners.containsKey(name)) {
            return;
        }

        outgoingListeners.put(name, listener);
    }

    public static void removeIncomingListener(String name) {
        if(!incomingListeners.containsKey(name)) {
            return;
        }

        incomingListeners.remove(name);
    }

    public static void removeOutgoingListener(String name) {
        if(!outgoingListeners.containsKey(name)) {
            return;
        }

        outgoingListeners.remove(name);
    }

    public static ChannelPipeline getIncomingPipeline() {
        return incomingPipeline;
    }

    public static ChannelPipeline getOutgoingPipeline() {
        return outgoingPipeline;
    }

    public static ChannelHandlerContext getIncomingChannelHandlerContext() {
        return incomingChannelHandlerContext;
    }

    public static ChannelHandlerContext getOutgoingChannelHandlerContext() {
        return outgoingChannelHandlerContext;
    }
}