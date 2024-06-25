package home.monitoring.sensors;

import java.util.Random;

public class TemperatureSensor extends Sensor<Double> {
    private boolean anomalyDetected = false;
    private double anomalyValue;


    public TemperatureSensor(double normalValue, double threshold) {
        super("Температура", normalValue, threshold);
    }

    @Override
    public void generateData() {
        if (isDeviceOff()) {
            return;
        }

        if (shouldDisableSensor()) {
            setDeviceOff(true);
            return;
        }

        Random random = new Random();

        if (shouldGenerateAnomaly() && !anomalyDetected) {
            // Генерация аномального значения
            anomalyValue = getNormalValue() + getThreshold() + 50;
            setCurrentValue(anomalyValue);
            setThresholdExceeded(true);
            anomalyDetected = true;
        } else if (anomalyDetected) {
            // Поддержка аномального значения
            setCurrentValue(anomalyValue + (random.nextDouble() - 0.5) * getThreshold());
        } else {
            // Генерация нормального значения
            double variation = (random.nextDouble() - 0.5) * getThreshold();
            double generatedValue = getNormalValue() + variation;
            setCurrentValue(generatedValue);
            setThresholdExceeded(checkThreshold());
        }
    }

    @Override
    public boolean checkThreshold() {
        return Math.abs(getCurrentValue() - getNormalValue()) > getThreshold();
    }

    @Override
    public String toString() {
        String formattedValue = df.format(getCurrentValue());
        return getType() +": "+ (isDeviceOff() ? "датчик отключен  " + getFormattedLastUpdateTime() : formattedValue +"℃  " + getFormattedLastUpdateTime());
    }
}
