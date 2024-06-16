package home.monitoring.sensors;

import java.util.Random;

public class PressureSensor extends Sensor<Double> {
    private boolean anomalyDetected = false;
    private double anomalyValue = 0.0;

    public PressureSensor(double normalValue, double threshold) {
        super("Давление", normalValue, threshold);
    }

    @Override
    public void generateData() {
        if (isDisabled()) {
            return;
        }

        Random random = new Random();

        if (shouldDisableSensor()) {
            setDisabled(true);
            return;
        }

        if (shouldGenerateAnomaly() && !anomalyDetected) {
            // Генерация аномального значения, если оно еще не было сгенерировано
            anomalyValue = getNormalValue() + getThreshold() * 2;
            setCurrentValue(anomalyValue);
            setAnomalous(true);
            anomalyDetected = true;
        } else if (anomalyDetected) {
            // Поддержка аномального значения
            setCurrentValue(anomalyValue + (random.nextDouble() - 0.5) * getThreshold());
        } else {
            // Генерация нормального значения
            double variation = (random.nextDouble() - 0.5) * getThreshold();
            double generatedValue = getNormalValue() + variation;
            setCurrentValue(generatedValue);
            setCurrentValue(generatedValue);
            setAnomalous(checkAnomaly());
        }
    }

    @Override
    public boolean checkAnomaly() {
        return Math.abs(getCurrentValue() - getNormalValue()) > getThreshold();
    }
}
