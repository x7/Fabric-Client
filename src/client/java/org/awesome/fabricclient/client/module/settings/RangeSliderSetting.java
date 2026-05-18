package org.awesome.fabricclient.client.module.settings;

public class RangeSliderSetting extends Setting<int[]> {
    private final int min;
    private final int max;

    public RangeSliderSetting(String name, String description, int defaultMin, int defaultMax, int min, int max) {
        super(name, description, new int[]{defaultMin, defaultMax});
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMinValue() {
        return value[0];
    }

    public int getMaxValue() {
        return value[1];
    }

    public void setMinValue(int v) {
        value[0] = Math.max(min, Math.min(v, value[1]));
    }

    public void setMaxValue(int v) {
        value[1] = Math.max(value[0], Math.min(v, max));
    }
}