package home.monitoring.sensors;

public class GasLeakSensor extends Sensor<Boolean> {
    private boolean anomalyDetected = false;

    public GasLeakSensor() {
        super("Газоанализатор", false, true);
    }

    @Override
    public void generateData() {
        if (isDisabled()) {
            return;
        }

        if (shouldDisableSensor()) {
            setDisabled(true);
            return;
        }

        if (shouldGenerateAnomaly() && !anomalyDetected) {
            setCurrentValue(true);
            setAnomalous(true);
            anomalyDetected = true;
        } else if (!anomalyDetected) {
            setCurrentValue(false);
            setAnomalous(false);
        }
    }

    @Override
    public boolean checkAnomaly() {
        return getCurrentValue();
    }

    @Override
    public String toString() {
        if (isDisabled()) {
            return getType() + ": датчик отключен";
        } else if (getCurrentValue()) {
            return getType() + ": обнаружена утечка газа!";
        } else {
            return getType() + ": норма";
        }
    }
}
