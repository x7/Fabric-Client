package org.awesome.fabricclient.client.utility.packets;

import io.netty.channel.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.awesome.fabricclient.client.utility.MinecraftUtility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<Object> getAllPacketFields(Packet<?> packet) {
//        Map<?, ?> values = new HashMap<>();
        List<Object> values = new ArrayList<>();
        Class<?> clazz = packet.getClass();

        while(clazz != null) {
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    values.add(field.get(packet));
//                    values.put(field.getName(), String.valueOf(field.get(packet)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

        return values;
    }
}