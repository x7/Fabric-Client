package org.awesome.fabricclient.client.module;

import org.awesome.fabricclient.client.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    protected final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
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

    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        VISUALS("Visuals");

        public final String displayName;
        Category(String displayName) {
            this.displayName = displayName;
        }
    }
}