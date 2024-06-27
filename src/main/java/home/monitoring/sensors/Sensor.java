package home.monitoring.sensors;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public abstract class Sensor<T> {
    private String type;
    private T currentValue;
    private T normalValue;
    private T threshold;
    //
    private boolean isThresholdExceeded;
    private boolean isDeviceOff;
    private double anomalyProbability = 0.05; // начальная вероятность 5%
    private double disableProbability = 0.05; // 5% вероятность отключения

    private LocalDateTime lastUpdateTime;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DecimalFormat df = new DecimalFormat("0.0");

    public Sensor(String type, T normalValue, T threshold) {
        this.type = type;
        this.normalValue = normalValue;
        this.threshold = threshold;
        this.isThresholdExceeded = false;
        this.isDeviceOff = false;
        this.currentValue = normalValue;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void increaseAnomalyProbability(double increment) {
        this.anomalyProbability += increment;
    }

    public abstract T generateAnomalyValue();

    public abstract T generateNormalValue(Random random);

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
            T anomalyValue = generateAnomalyValue();
            setCurrentValue(anomalyValue);
            setThresholdExceeded(true);
            isThresholdExceeded = true;
        } else if (isThresholdExceeded) {
            // Поддержка аномального значения
            T anomalyValue = generateAnomalyValue();
            setCurrentValue(anomalyValue);
        } else {
            // Генерация нормального значения
            T generatedValue = generateNormalValue(random);
            setCurrentValue(generatedValue);
            setThresholdExceeded(checkThreshold());
        }
    }

    protected boolean shouldGenerateAnomaly() {
        Random random = new Random();
        return random.nextDouble() < anomalyProbability;
    }

    protected boolean shouldDisableSensor() {
        Random random = new Random();
        return random.nextDouble() < disableProbability;
    }

    public String getFormattedLastUpdateTime() {
        return lastUpdateTime.format(dateFormatter);
    }

    @Override
    public String toString() {
        return type + " (Текущее значение: " + (isDeviceOff ? "Отключен" : currentValue.toString()) + ")";
    }

    public abstract boolean checkThreshold();

    public abstract String outputStatus();

    public T getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(T currentValue) {
        this.currentValue = currentValue;
    }

    public T getNormalValue() {
        return normalValue;
    }

    public T getThreshold() {
        return threshold;
    }

    public boolean isDeviceOff() {
        return isDeviceOff;
    }

    public void setDeviceOff(boolean isDeviceOff) {
        this.isDeviceOff = isDeviceOff;
    }

    public void setThresholdExceeded(boolean isThresholdExceeded) {
        this.isThresholdExceeded = isThresholdExceeded;
    }

    public boolean isThresholdExceeded() {
        return isThresholdExceeded;
    }

    public String getType() {
        return type;
    }
    public void enableSensor() {
        this.isDeviceOff = false;
    }
}
