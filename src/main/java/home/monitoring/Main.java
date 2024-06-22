package home.monitoring;

import home.monitoring.sensors.GasLeakSensor;
import home.monitoring.sensors.PressureSensor;
import home.monitoring.sensors.TemperatureSensor;
import home.monitoring.systems.HomeEngineeringSystem;
import home.monitoring.ui.MonitoringUI;

public class Main {
    public static void main(String[] args) {
        // Создание инженерных систем и добавление датчиков
        HomeEngineeringSystem boiler = new HomeEngineeringSystem("Котел");
        boiler.addSensor(new TemperatureSensor(55, 5));
        //boiler.addSensor(new GasLeakSensor());

//        HomeEngineeringSystem pipeline = new HomeEngineeringSystem("Трубопровод");
//        pipeline.addSensor(new PressureSensor(3, 1.5));
//        pipeline.addSensor(new GasLeakSensor());

        // Запуск пользовательского интерфейса
        MonitoringUI monitoringUI = new MonitoringUI();
        monitoringUI.addSystem(boiler);
//        monitoringUI.addSystem(pipeline);
        monitoringUI.createAndShowGUI();
    }
}
