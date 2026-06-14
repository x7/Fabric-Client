package org.awesome.fabricclient.client.utility.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.awesome.fabricclient.client.utility.MinecraftUtility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PacketUtilitys {
    public static Channel getChannel() {
        try {
            Minecraft minecraft = MinecraftUtility.getMinecraftClient();
            Field channelField = Connection.class.getDeclaredField("channel");
            channelField.setAccessible(true);

            return (Channel) channelField.get(minecraft.getConnection().getConnection());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Object> getAllPacketFields(Packet<?> packet) {
        List<Object> values = new ArrayList<>();
        Class<?> clazz = packet.getClass();

        while(clazz != null) {
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    values.add(field.get(packet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

        return values;
    }

    public static void sendPacketToServer(Packet<?> packet) {
        PacketFlow packetFlow = packet.type().flow();
        if(packetFlow != PacketFlow.SERVERBOUND) {
            System.out.println("Sending a non server bound packet is not allowed");
            return;
        }

        ClientPacketListener clientPacketListener = MinecraftUtility.getPacketListener();
        clientPacketListener.send(packet);
    }

    public static void sendPacketToClient(Packet<?> packet) {
        PacketFlow packetFlow = packet.type().flow();
        if(packetFlow != PacketFlow.CLIENTBOUND) {
            System.out.println("Sending a non client bound packet is not allowed");
            return;
        }

        ChannelHandlerContext channelHandlerContext = PacketManager.getIncomingChannelHandlerContext();
        channelHandlerContext.fireChannelRead(packet);
    }
}