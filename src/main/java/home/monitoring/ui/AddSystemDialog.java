package home.monitoring.ui;

import home.monitoring.sensors.*;
import home.monitoring.systems.HomeEngineeringSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddSystemDialog {
    private JFrame frame;
    private MonitoringUI monitoringUI;

    public AddSystemDialog(JFrame frame, MonitoringUI monitoringUI) {
        this.frame = frame;
        this.monitoringUI = monitoringUI;
    }

    protected void showAddSystemDialog() {
        JDialog dialog = new JDialog(frame, "Добавить систему", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 600);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Имя системы:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(nameField, gbc);

        JLabel sensorsLabel = new JLabel("Выберите датчики и укажите пороговые значения (если применимо):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        inputPanel.add(sensorsLabel, gbc);

        JCheckBox gasLeakSensor = new JCheckBox("Газоанализатор");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        inputPanel.add(gasLeakSensor, gbc);

        // Создание панелей для сенсоров
        JCheckBox temperatureSensor = new JCheckBox("Температура (℃)");
        gbc.gridy = 3;
        inputPanel.add(temperatureSensor, gbc);

        JPanel temperaturePanel = createSensorPanel();
        gbc.gridy = 4;
        inputPanel.add(temperaturePanel, gbc);

        JCheckBox pressureSensor = new JCheckBox("Давление (бар)");
        gbc.gridy = 5;
        inputPanel.add(pressureSensor, gbc);

        JPanel pressurePanel = createSensorPanel();
        gbc.gridy = 6;
        inputPanel.add(pressurePanel, gbc);

        JCheckBox energySensor = new JCheckBox("Энергопотребление (кВт)");
        gbc.gridy = 7;
        inputPanel.add(energySensor, gbc);

        JPanel energyPanel = createSensorPanel();
        gbc.gridy = 8;
        inputPanel.add(energyPanel, gbc);

        JCheckBox gasSensor = new JCheckBox("Потребление газа (м³/ч)");
        gbc.gridy = 9;
        inputPanel.add(gasSensor, gbc);

        JPanel gasPanel = createSensorPanel();
        gbc.gridy = 10;
        inputPanel.add(gasPanel, gbc);

        JCheckBox humiditySensor = new JCheckBox("Влажность (%)");
        gbc.gridy = 11;
        inputPanel.add(humiditySensor, gbc);

        JPanel humidityPanel = createSensorPanel();
        gbc.gridy = 12;
        inputPanel.add(humidityPanel, gbc);

        JCheckBox noiseSensor = new JCheckBox("Шум (дБ)");
        gbc.gridy = 13;
        inputPanel.add(noiseSensor, gbc);

        JPanel noisePanel = createSensorPanel();
        gbc.gridy = 14;
        inputPanel.add(noisePanel, gbc);

        JCheckBox vibrationSensor = new JCheckBox("Вибрация (мм/с)");
        gbc.gridy = 15;
        inputPanel.add(vibrationSensor, gbc);

        JPanel vibrationPanel = createSensorPanel();
        gbc.gridy = 16;
        inputPanel.add(vibrationPanel, gbc);

        // Добавление прокрутки
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Изначально скрываем все панели сенсоров
        temperaturePanel.setVisible(false);
        pressurePanel.setVisible(false);
        energyPanel.setVisible(false);
        gasPanel.setVisible(false);
        humidityPanel.setVisible(false);
        noisePanel.setVisible(false);
        vibrationPanel.setVisible(false);

        // Добавление слушателей для чекбоксов
        temperatureSensor.addActionListener(e -> temperaturePanel.setVisible(temperatureSensor.isSelected()));
        pressureSensor.addActionListener(e -> pressurePanel.setVisible(pressureSensor.isSelected()));
        energySensor.addActionListener(e -> energyPanel.setVisible(energySensor.isSelected()));
        gasSensor.addActionListener(e -> gasPanel.setVisible(gasSensor.isSelected()));
        humiditySensor.addActionListener(e -> humidityPanel.setVisible(humiditySensor.isSelected()));
        noiseSensor.addActionListener(e -> noisePanel.setVisible(noiseSensor.isSelected()));
        vibrationSensor.addActionListener(e -> vibrationPanel.setVisible(vibrationSensor.isSelected()));

        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String systemName = nameField.getText();
                if (systemName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Имя системы не может быть пустым.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean isSensorSelected = gasLeakSensor.isSelected() || temperatureSensor.isSelected() || pressureSensor.isSelected() ||
                        energySensor.isSelected() || gasSensor.isSelected() || humiditySensor.isSelected() ||
                        noiseSensor.isSelected() || vibrationSensor.isSelected();

                if (!isSensorSelected) {
                    JOptionPane.showMessageDialog(dialog, "Вы должны выбрать хотя бы один датчик.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                HomeEngineeringSystem newSystem = new HomeEngineeringSystem(systemName);
                try {
                    if (gasLeakSensor.isSelected()) newSystem.addSensor(new GasLeakSensor());
                    if (temperatureSensor.isSelected()) {
                        double temperatureNormal = parseDouble(((JTextField) temperaturePanel.getComponent(1)).getText());
                        double temperatureThreshold = parseDouble(((JTextField) temperaturePanel.getComponent(3)).getText());
                        newSystem.addSensor(new TemperatureSensor(temperatureNormal, temperatureThreshold));
                    }
                    if (pressureSensor.isSelected()) {
                        double pressureNormal = parseDouble(((JTextField) pressurePanel.getComponent(1)).getText());
                        double pressureThreshold = parseDouble(((JTextField) pressurePanel.getComponent(3)).getText());
                        newSystem.addSensor(new PressureSensor(pressureNormal, pressureThreshold));
                    }
                    if (energySensor.isSelected()) {
                        double energyNormal = parseDouble(((JTextField) energyPanel.getComponent(1)).getText());
                        double energyThreshold = parseDouble(((JTextField) energyPanel.getComponent(3)).getText());
                        newSystem.addSensor(new EnergyConsumptionSensor(energyNormal, energyThreshold));
                    }
                    if (gasSensor.isSelected()) {
                        double gasNormal = parseDouble(((JTextField) gasPanel.getComponent(1)).getText());
                        double gasThreshold = parseDouble(((JTextField) gasPanel.getComponent(3)).getText());
                        newSystem.addSensor(new GasConsumptionSensor(gasNormal, gasThreshold));
                    }
                    if (humiditySensor.isSelected()) {
                        double humidityNormal = parseDouble(((JTextField) humidityPanel.getComponent(1)).getText());
                        double humidityThreshold = parseDouble(((JTextField) humidityPanel.getComponent(3)).getText());
                        newSystem.addSensor(new HumiditySensor(humidityNormal, humidityThreshold));
                    }
                    if (noiseSensor.isSelected()) {
                        double noiseNormal = parseDouble(((JTextField) noisePanel.getComponent(1)).getText());
                        double noiseThreshold = parseDouble(((JTextField) noisePanel.getComponent(3)).getText());
                        newSystem.addSensor(new NoiseSensor(noiseNormal, noiseThreshold));
                    }
                    if (vibrationSensor.isSelected()) {
                        double vibrationNormal = parseDouble(((JTextField) vibrationPanel.getComponent(1)).getText());
                        double vibrationThreshold = parseDouble(((JTextField) vibrationPanel.getComponent(3)).getText());
                        newSystem.addSensor(new VibrationSensor(vibrationNormal, vibrationThreshold));
                    }

                    monitoringUI.addSystem(newSystem);
                    monitoringUI.addSystemToTree(newSystem);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Ошибка: все пороговые значения должны быть числовыми.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(addButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createSensorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel normalValueLabel = new JLabel("Нормальное значение:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(normalValueLabel, gbc);

        JTextField normalField = new JTextField();
        gbc.gridy = 1;
        panel.add(normalField, gbc);

        JLabel thresholdValueLabel = new JLabel("Допустимый порог отклонения:");
        gbc.gridy = 2;
        panel.add(thresholdValueLabel, gbc);

        JTextField thresholdField = new JTextField();
        gbc.gridy = 3;
        panel.add(thresholdField, gbc);

        return panel;
    }

    private double parseDouble(String str) throws NumberFormatException {
        return Double.parseDouble(str);
    }
}
