package home.monitoring.sensors;

import java.util.Random;

public class TemperatureSensor extends Sensor<Double> {

    public TemperatureSensor(double normalValue, double threshold) {
        super("Температура", normalValue, threshold);
    }

    @Override
    public Double generateAnomalyValue() {
        return getNormalValue() + getThreshold() + 50;
    }

    @Override
    public Double generateNormalValue(Random random) {
        double variation = (random.nextDouble() - 0.5) * getThreshold();
        return getNormalValue() + variation;
    }

    @Override
    public boolean checkThreshold() {
        return Math.abs(getCurrentValue() - getNormalValue()) > getThreshold();
    }

    @Override
    public String outputStatus() {
        if (isDeviceOff()) {
            return "датчик отключен";
        }
        return df.format(getCurrentValue()) + " °C";
    }

    @Override
    public String toString() {
        String formattedValue = df.format(getCurrentValue());
        return getType() + ": " + (isDeviceOff() ? "датчик отключен  " + getFormattedLastUpdateTime() : formattedValue + " °C  " + getFormattedLastUpdateTime());
    }
}
