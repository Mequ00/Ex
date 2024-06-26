package home.monitoring;

import home.monitoring.sensors.*;
import home.monitoring.systems.HomeEngineeringSystem;
import home.monitoring.ui.MonitoringUI;

public class Main {
    public static void main(String[] args) {
        HomeEngineeringSystem gasPipeline = new HomeEngineeringSystem("Газопровод");
        gasPipeline.addSensor(new PressureSensor(4, 1));
        gasPipeline.addSensor(new GasLeakSensor());

//        HomeEngineeringSystem boiler = new HomeEngineeringSystem("Котел");
//        boiler.addSensor(new TemperatureSensor(55, 5));
//        boiler.addSensor(new GasConsumptionSensor(2,0.5));
//        boiler.addSensor(new GasLeakSensor());
//
//        HomeEngineeringSystem pumpingStation = new HomeEngineeringSystem("Насосная станция");
//        pumpingStation.addSensor(new PressureSensor(5,5));
//        pumpingStation.addSensor(new HumiditySensor(40,20));
//        pumpingStation.addSensor(new VibrationSensor(3,1));
//        pumpingStation.addSensor(new EnergyConsumptionSensor(3,1));
//
//        HomeEngineeringSystem conditioner = new HomeEngineeringSystem("Кондиционер");
//        conditioner.addSensor(new TemperatureSensor(60, 10));
//        conditioner.addSensor(new VibrationSensor(2, 1));
//        conditioner.addSensor(new NoiseSensor(50, 10));
//        conditioner.addSensor(new EnergyConsumptionSensor(1, 0.5));
//
//        HomeEngineeringSystem transformer = new HomeEngineeringSystem("Трансформатор");
//        transformer.addSensor(new TemperatureSensor(50,10));
//        //потребление в реальном времени всего дома
//        transformer.addSensor(new EnergyConsumptionSensor(5,3));
//        transformer.addSensor(new NoiseSensor(45,5));
//        transformer.addSensor(new VibrationSensor(1, 0.5));
//
//        HomeEngineeringSystem heating = new HomeEngineeringSystem("Отопление");
//        heating.addSensor(new TemperatureSensor(60, 10));
//        heating.addSensor(new PressureSensor(1,0.5));
//        heating.addSensor(new HumiditySensor(40, 10));

//        heating.saveReportToExcel("C:\\Users\\hasis\\IdeaProjects\\HomeEngineeringMonitor\\src\\main\\resources\\sensor_report.xlsx");
//        System.out.println("Отчет сохранен в sensor_report.xlsx");


        // Запуск пользовательского интерфейса
        MonitoringUI monitoringUI = new MonitoringUI();
        monitoringUI.addSystem(gasPipeline);
//        monitoringUI.addSystem(boiler);
//        monitoringUI.addSystem(pumpingStation);
//        monitoringUI.addSystem(conditioner);
//        monitoringUI.addSystem(transformer);
//        monitoringUI.addSystem(heating);

        monitoringUI.createAndShowGUI();
    }
}
