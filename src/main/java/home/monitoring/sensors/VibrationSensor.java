package home.monitoring.sensors;

import java.util.Random;

public class VibrationSensor extends Sensor<Double> {

    public VibrationSensor(double normalValue, double threshold) {
        super("Вибрация", normalValue, threshold);
    }

    @Override
    public Double generateAnomalyValue() {
        return getNormalValue() + getThreshold() * 1.8;
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
        return df.format(getCurrentValue()) + " мм/с";
    }

    @Override
    public String toString() {
        String formattedValue = df.format(getCurrentValue());
        return getType() + ": " + (isDeviceOff() ? "датчик отключен  " + getFormattedLastUpdateTime() : formattedValue + " мм/с  " + getFormattedLastUpdateTime());
    }
}
