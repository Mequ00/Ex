package home.monitoring.sensors;

import java.util.Random;


public class EnergyConsumptionSensor extends Sensor<Double> {
    private boolean isThresholdExceeded = false;
    private double anomalyValue;

    public EnergyConsumptionSensor(double normalValue, double threshold) {
        super("Энергопотребление", normalValue, threshold);
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

        if (shouldGenerateAnomaly() && !isThresholdExceeded) {
            // Генерация аномального значения
            anomalyValue = getNormalValue() + getThreshold() * 2;
            setCurrentValue(anomalyValue);
            setThresholdExceeded(true);
            isThresholdExceeded = true;
        } else if (isThresholdExceeded) {
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
    public String outputStatus() {
        if (isDeviceOff()) {
            return "датчик отключен";
        }
        return df.format(getCurrentValue()) + " кВт";
    }

    @Override
    public String toString() {
        String formattedValue = df.format(getCurrentValue());
        return getType() + ": " + (isDeviceOff() ? "датчик отключен  " + getFormattedLastUpdateTime() : formattedValue + " кВт  " + getFormattedLastUpdateTime());
    }
}
