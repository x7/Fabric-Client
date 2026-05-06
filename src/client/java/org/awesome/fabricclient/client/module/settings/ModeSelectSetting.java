package org.awesome.fabricclient.client.module.settings;

import java.util.List;

public class ModeSelectSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSelectSetting(String name, String description, String defaultValue, String... modes) {
        super(name, description, defaultValue);
        this.modes = List.of(modes);
    }

    public List<String> getModes() { return modes; }

    public void cycle() {
        int index = modes.indexOf(value);
        value = modes.get((index + 1) % modes.size());
    }
}