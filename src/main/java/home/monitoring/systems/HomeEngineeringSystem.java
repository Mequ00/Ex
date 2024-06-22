package home.monitoring.systems;

import home.monitoring.sensors.Sensor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class HomeEngineeringSystem {
    private String name;
    private List<Sensor<?>> sensors;
    private boolean anomalyDetected;
    private Path tempLogFile;
    private BufferedWriter writer;

    Path logsDir = Paths.get("src/main/resources/logs");;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH.mm.ss");

    // константа для максимального количества записей во временном файле
    private static final int MAX_LOG_ENTRIES = 5;

    public HomeEngineeringSystem(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.anomalyDetected = false;

        try {
            tempLogFile = Files.createTempFile(name + "_log", ".txt");
            writer = Files.newBufferedWriter(tempLogFile);
            System.out.println(tempLogFile);

            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void increaseAnomalyProbability(double increment) {
        for (Sensor<?> sensor : sensors) {
            if (!sensor.isDisabled()) {
                sensor.increaseAnomalyProbability(increment);
            }
        }
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

    public void writeToTempLogFile(String message) {
        try {
            Deque<String> logEntries = new LinkedList<>();

            if (Files.exists(tempLogFile)) {
                List<String> existingEntries = Files.readAllLines(tempLogFile);
                logEntries.addAll(existingEntries);
            }

            // Добавляем новую запись и ограничиваем размер до MAX_LOG_ENTRIES
            logEntries.addLast(message);
            if (logEntries.size() > MAX_LOG_ENTRIES) {
                logEntries.removeFirst();
            }

            // Записываем обновленные записи обратно в файл
            Files.write(tempLogFile, logEntries);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToLogFile(int countMessages) {
        try {
            Path logFile = logsDir.resolve(this.name+ LocalDateTime.now().format(dateFormatter) + ".txt");

            List<String> lines = Files.readAllLines(tempLogFile);

            // Получаем последние countMessages строк
            int start = Math.max(0, lines.size() - (countMessages));
            List<String> lastLines = lines.subList(start, lines.size());

            // Записываем последние countMessages строк в новый файл
            Files.write(logFile, lastLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeLogFile() {
        try {
            if (writer != null) {
                writer.close();
                Files.deleteIfExists(tempLogFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getTempLogFile() {
        return tempLogFile;
    }

    public boolean isAnomalous() {
        return anomalyDetected;
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

}
