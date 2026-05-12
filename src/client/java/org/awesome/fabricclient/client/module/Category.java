package org.awesome.fabricclient.client.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    VISUALS("Visuals"),
    UTILITY("Utility");

    public final String displayName;
    Category(String displayName) {
        this.displayName = displayName;
    }
}
