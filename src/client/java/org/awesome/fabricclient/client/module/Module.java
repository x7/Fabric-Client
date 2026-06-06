package org.awesome.fabricclient.client.module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.settings.Setting;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

import java.util.*;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private final boolean active;
    private boolean enabled = false;
    protected final Map<String, Setting<?>> settings = new LinkedHashMap<>();
    private boolean isStartTickInitialized = false;
    private boolean isEndTickInitialized = false;
    private boolean isIncomingPacketHandlerInitialized;
    private boolean isOutgoingPacketHandlerInitialized;

    public Module() {
        RegisterModule annotation = this.getClass().getAnnotation(RegisterModule.class);

        if(annotation == null) {
            throw new IllegalStateException(this.getClass().getName() + " is missing @RegisterModule annotation");
        }

        this.name = annotation.name();
        this.description = annotation.description();
        this.category = annotation.category();
        this.active = annotation.active();
    }

    protected Module(String name, String description, Category category, boolean active) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.active = active;
    }

    public void toggle() {
        enabled = !enabled;

        if(!enabled) {
            onDisable();
            return;
        }

        onEnable();

        // Start Tick Event
        if(!isStartTickInitialized) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if(eventChecks()) {
                    return;
                }

                onTickStart();
            });

            isStartTickInitialized = true;
        }

        // End Tick Event
        if(!isEndTickInitialized) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(eventChecks()) {
                    return;
                }

                onTickEnd();
            });

            isEndTickInitialized = true;
        }

        // Incoming packet
        if(!isIncomingPacketHandlerInitialized) {
            PacketManager.addIncomingListener("module_incoming_packet_listener", packetEvent -> {
                if(eventChecks()) {
                    return;
                }

                onPacketReceive(packetEvent);
            });

            isIncomingPacketHandlerInitialized = true;
        }

        // Outgoing Packet
        if(!isOutgoingPacketHandlerInitialized) {
            PacketManager.addOutgoingListener("module_outgoing_packet_listener", packetEvent -> {
                if(eventChecks()) {
                    return;
                }

                onPacketSend(packetEvent);
            });

            isOutgoingPacketHandlerInitialized = true;
        }
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void onTickStart() {

    }

    public void onTickEnd() {

    }

    public void onPacketSend(PacketEvent packetEvent) {

    }

    public void onPacketReceive(PacketEvent packetEvent) {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isActive() {
        return active;
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.put(setting.getName(), setting);
        return setting;
    }

    public Setting<?> getSetting(String settingName) {
        return settings.get(settingName);
    }

    public List<Setting<?>> getSettings() {
        List<Setting<?>> settingsList = new ArrayList<>();

        for(Map.Entry<String, Setting<?>> entry : settings.entrySet()) {
            if(settingsList.contains(entry.getValue())) {
                continue;
            }

            settingsList.add(entry.getValue());
        }

        return settingsList;
    }

    private boolean eventChecks() {
        Player player = PlayerUtility.getPlayer();
        return player == null || !isEnabled();
    }
}