package home.monitoring.sensors;

public class GasLeakSensor extends Sensor<Boolean> {
    private boolean anomalyDetected = false;

    public GasLeakSensor() {
        super("Газоанализатор", false, true);
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

        if (shouldGenerateAnomaly() && !anomalyDetected) {
            setCurrentValue(true);
            setThresholdExceeded(true);
            anomalyDetected = true;
        } else if (!anomalyDetected) {
            setCurrentValue(false);
            setThresholdExceeded(false);
        }
    }

    @Override
    public boolean checkThreshold() {
        return getCurrentValue();
    }

    @Override
    public String toString() {
        if (isDeviceOff()) {
            return getType() + ": датчик отключен  " + getFormattedLastUpdateTime();
        } else if (getCurrentValue()) {
            return getType() + ": обнаружена утечка газа!  " + getFormattedLastUpdateTime();
        } else {
            return getType() + ": норма  "  + getFormattedLastUpdateTime();
        }
    }
}
