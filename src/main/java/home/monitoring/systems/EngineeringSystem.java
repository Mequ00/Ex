package home.monitoring.systems;

import home.monitoring.sensors.Sensor;

import java.util.ArrayList;
import java.util.List;

public class EngineeringSystem {
    private String name;
    private List<Sensor<?>> sensors;
    private boolean anomalyDetected;

    public EngineeringSystem(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.anomalyDetected = false;
    }

    public String getName() {
        return name;
    }

    public List<Sensor<?>> getSensors() {
        return sensors;
    }

    public void addSensor(Sensor<?> sensor) {
        sensors.add(sensor);
    }

    public void generateData() {
        for (Sensor<?> sensor : sensors) {
            sensor.generateData();
            if (sensor.isAnomalous()) {
                anomalyDetected = true;
                increaseAnomalyProbability(0.30);
            }
        }
    }

    private void increaseAnomalyProbability(double increment) {
        for (Sensor<?> sensor : sensors) {
            if (!sensor.isDisabled()) {
                sensor.increaseAnomalyProbability(increment);
            }
        }
    }

    public boolean isAnomalous() {
        return anomalyDetected;
    }
}
