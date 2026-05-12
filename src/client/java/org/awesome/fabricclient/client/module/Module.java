package org.awesome.fabricclient.client.module;

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

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void toggle() {
        enabled = !enabled;

        if(enabled) {
            onEnable();
            return;
        }

        onDisable();
    }

    public void onEnable() {

    }

    public void onDisable() {

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
        List<Setting<?>> settings = getSettings();
        Setting<?> returnSetting = null;

        for(Setting<?> setting : settings) {
            if(setting.getName().equalsIgnoreCase(settingName)) {
                returnSetting = setting;
                break;
            }
        }

        return returnSetting;
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