package org.awesome.fabricclient.client.module.settings;

public class RangeSliderSetting extends Setting<double[]> {
    private final double min;
    private final double max;

    public RangeSliderSetting(String name, String description, double defaultMin, double defaultMax, double min, double max) {
        super(name, description, new double[]{defaultMin, defaultMax});
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMinValue() {
        return value[0];
    }

    public double getMaxValue() {
        return value[1];
    }

    public void setMinValue(double v) {
        value[0] = Math.max(min, Math.min(v, value[1]));
    }

    public void setMaxValue(double v) {
        value[1] = Math.max(value[0], Math.min(v, max));
    }
}