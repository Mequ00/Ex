package home.monitoring.sensors;

public class GasLeakSensor extends Sensor<Boolean> {
    private boolean isThresholdExceeded = false;

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

        if (shouldGenerateAnomaly() && !isThresholdExceeded) {
            setCurrentValue(true);
            setThresholdExceeded(true);
            isThresholdExceeded = true;
        } else if (!isThresholdExceeded) {
            setCurrentValue(false);
            setThresholdExceeded(false);
        }
    }

    @Override
    public boolean checkThreshold() {
        return getCurrentValue();
    }

    @Override
    public String outputStatus() {
        if (isDeviceOff()) {
            return "датчик отключен";
        } else if (getCurrentValue()) {
            return "обнаружена утечка газа!";
        } else {
            return "норма";
        }
    }

    @Override
    public String toString() {
        return getType() + ": " + outputStatus() + " " + getFormattedLastUpdateTime();
    }
}
