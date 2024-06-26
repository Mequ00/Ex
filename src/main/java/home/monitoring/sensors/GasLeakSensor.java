package home.monitoring.sensors;

import java.util.Random;

public class GasLeakSensor extends Sensor<Boolean> {

    public GasLeakSensor() {
        super("Газоанализатор", false, true);
    }

    @Override
    public Boolean generateAnomalyValue() {
        return true;
    }

    @Override
    public Boolean generateNormalValue(Random random) {
        return false;
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
