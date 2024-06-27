package home.monitoring;

import home.monitoring.sensors.*;
import home.monitoring.systems.HomeEngineeringSystem;
import home.monitoring.ui.MonitoringUI;

public class Main {
    public static void main(String[] args) {
        HomeEngineeringSystem gasPipeline = new HomeEngineeringSystem("Газопровод");
        gasPipeline.addSensor(new PressureSensor(4, 1));
        gasPipeline.addSensor(new GasLeakSensor());

        HomeEngineeringSystem boiler = new HomeEngineeringSystem("Котел");
        boiler.addSensor(new TemperatureSensor(55, 5));
        boiler.addSensor(new GasConsumptionSensor(2,0.5));
        boiler.addSensor(new GasLeakSensor());


        // Запуск пользовательского интерфейса
        MonitoringUI monitoringUI = new MonitoringUI();
        monitoringUI.addSystem(gasPipeline);
        monitoringUI.addSystem(boiler);

        monitoringUI.createAndShowGUI();
    }
}
