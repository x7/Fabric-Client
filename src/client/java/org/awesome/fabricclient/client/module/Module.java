package org.awesome.fabricclient.client.module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.awesome.fabricclient.client.module.settings.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    protected final Map<String, Setting<?>> settings = new HashMap<>();
    private boolean isStartTickInitialized = false;
    private boolean isEndTickInitialized = false;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void toggle() {
        enabled = !enabled;

        if(!isStartTickInitialized) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if(!isEnabled()) {
                    System.out.println("not enabled");
                    return;
                }

                onTickStart();
            });

            isStartTickInitialized = true;
        }

        if(!isEndTickInitialized) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(!isEnabled()) {
                    return;
                }

                onTickEnd();
            });

            isEndTickInitialized = true;
        }

        if(!enabled) {
            onDisable();
            return;
        }

        onEnable();
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void onTickStart() {

    }

    public void onTickEnd() {

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
}