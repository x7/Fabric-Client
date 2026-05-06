package org.awesome.fabricclient.client.module.settings;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    protected T value;
    private BooleanSupplier visible = () -> true;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }

    public boolean isVisible() { return visible.getAsBoolean(); }

    public Setting<T> visibleWhen(BooleanSupplier predicate) {
        this.visible = predicate;
        return this;
    }
}
