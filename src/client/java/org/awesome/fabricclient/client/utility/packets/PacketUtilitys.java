package org.awesome.fabricclient.client.utility.packets;

import io.netty.channel.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.awesome.fabricclient.client.utility.MinecraftUtility;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public static ConcurrentMap<String, String> getAllPacketFields(Packet<?> packet) {
        ConcurrentMap<String, String> values = new ConcurrentHashMap<>();
        Class<?> clazz = packet.getClass();

        while(clazz != null) {
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    values.put(field.getName(), String.valueOf(field.get(packet)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

        return values;
    }
}