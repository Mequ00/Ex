package home.monitoring.systems;

import home.monitoring.sensors.Sensor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

    private boolean thresholdExceededDetected;

    private boolean isAnyAnomalyDetected;

    private Deque<String> logEntries = new LinkedList<>();


    Path logsDir = Paths.get("src/main/resources/logs");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HHчmmмssс");

    // константа для максимального количества записей во временном файле
    private static final int MAX_LOG_ENTRIES = 20;

    public HomeEngineeringSystem(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.thresholdExceededDetected = false;
        this.isAnyAnomalyDetected = false;

        try {
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void increaseAnomalyProbability(double increment) {
        for (Sensor<?> sensor : sensors) {
            if (!sensor.isDeviceOff()) {
                sensor.increaseAnomalyProbability(increment);
            }
        }
    }

    public void generateData() {
        for (Sensor<?> sensor : sensors) {
            sensor.generateData();
            if (sensor.isThresholdExceeded()) {
                thresholdExceededDetected = true;
                increaseAnomalyProbability(0.30);
            }
        }
    }

    public void writeToTempLog(String message) {
            // Добавляем новую запись и ограничиваем размер до MAX_LOG_ENTRIES
            logEntries.addLast(message);
            if (logEntries.size() > MAX_LOG_ENTRIES) {
                logEntries.removeFirst();
            }
    }

    public void writeToLogFile(int countMessages) {
        try {
            Path logFile = logsDir.resolve(this.name + LocalDateTime.now().format(dateFormatter) + ".txt");

            List<String> lines = new ArrayList<>(logEntries);

            // Получаем последние countMessages строк
            int start = Math.max(0, lines.size() - (countMessages));
            List<String> lastLines = lines.subList(start, lines.size());

            // Записываем последние countMessages строк в новый файл
            Files.write(logFile, lastLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkAnyAnomaly() {
        for (Sensor<?> sensor : sensors) {
            if (sensor.isDeviceOff() || sensor.isThresholdExceeded()) {
                this.isAnyAnomalyDetected = true;
                writeToLogFile(5);
                break;
            }
        }
    }


    public void saveReportToExcel(String filePath) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        filePath += "_" + timestamp + ".xlsx";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчет" + getName() + LocalDateTime.now().format(dateFormatter) + ".xlsx");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Тип датчика");
        headerRow.createCell(1).setCellValue("Значение");
        headerRow.createCell(2).setCellValue("Время");

        for (Sensor<?> sensor : sensors) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sensor.getType());
            row.createCell(1).setCellValue(sensor.outputStatus());
            row.createCell(2).setCellValue(sensor.getFormattedLastUpdateTime());
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public boolean isAnyAnomalyDetected() {
        return isAnyAnomalyDetected;
    }

    public void setAnyAnomalyDetected(boolean anyAnomalyDetected) {
        isAnyAnomalyDetected = anyAnomalyDetected;
    }

    @Override
    public String toString() {
        return name;
    }
}
