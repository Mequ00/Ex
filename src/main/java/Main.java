import home.monitoring.sensors.GasLeakSensor;
import home.monitoring.sensors.PressureSensor;
import home.monitoring.sensors.TemperatureSensor;
import home.monitoring.systems.EngineeringSystem;
import home.monitoring.ui.MonitoringUI;

public class Main {
    public static void main(String[] args) {
        // Создание инженерных систем и добавление датчиков
        EngineeringSystem boiler = new EngineeringSystem("Котел");
        boiler.addSensor(new TemperatureSensor(300, 10));
        boiler.addSensor(new GasLeakSensor());

//        EngineeringSystem pipeline = new EngineeringSystem("Трубопровод");
//        pipeline.addSensor(new PressureSensor(5, 0.5));
//        pipeline.addSensor(new GasLeakSensor());

        // Запуск пользовательского интерфейса
        MonitoringUI monitoringUI = new MonitoringUI();
        monitoringUI.addSystem(boiler);
//        monitoringUI.addSystem(pipeline);
        monitoringUI.createAndShowGUI();
    }
}
