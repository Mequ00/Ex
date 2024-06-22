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

    private LocalDateTime lastUpdateTime;
    private boolean isAnomalous;
    private double anomalyProbability = 0.05; // начальная вероятность 5%
    private boolean isDisabled;
    private double disableProbability = 0.05; // 5% вероятность отключения

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DecimalFormat df = new DecimalFormat("#.0");

    public Sensor(String type, T normalValue, T threshold) {
        this.type = type;
        this.normalValue = normalValue;
        this.threshold = threshold;
        this.isAnomalous = false;
        this.isDisabled = false;
        this.currentValue = normalValue;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void increaseAnomalyProbability(double increment) {
        this.anomalyProbability += increment;
    }

    public void disableSensor() {
        this.isDisabled = true;
    }

    public void enableSensor() {
        this.isDisabled = false;
    }

    public abstract void generateData();

    public abstract boolean checkAnomaly();


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
        return type + " (Текущее значение: " + (isDisabled ? "Отключен" : currentValue.toString()) + ")";
    }

    public String getType() {
        return type;
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(T currentValue) {
        this.currentValue = currentValue;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public T getNormalValue() {
        return normalValue;
    }

    public T getThreshold() {
        return threshold;
    }



    public boolean isAnomalous() {
        return isAnomalous;
    }

    public void setAnomalous(boolean anomalous) {
        isAnomalous = anomalous;
    }


    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public double getAnomalyProbability() {
        return anomalyProbability;
    }

}
