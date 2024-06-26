package home.monitoring.ui;

import home.monitoring.sensors.*;
import home.monitoring.systems.HomeEngineeringSystem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MonitoringUI {
    private JFrame frame;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JTextArea logArea;

    private List<HomeEngineeringSystem> systems = new ArrayList<>();

    public void addSystem(HomeEngineeringSystem system) {
        systems.add(system);
    }

    public void createAndShowGUI() {
        frame = new JFrame("Мониторинг инженерных систем");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Инженерные системы");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        SensorTreeCellRenderer renderer = new SensorTreeCellRenderer();
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        renderer.setLeafIcon(null);
        tree.setCellRenderer(renderer);
        JScrollPane treeScrollPane = new JScrollPane(tree);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        JButton refreshButton = new JButton("Получить данные");
        refreshButton.addActionListener(e -> updateAllSystemData());

        JButton addButton = new JButton("Добавить систему");
        addButton.addActionListener(e -> new AddSystemDialog(frame, this).showAddSystemDialog());


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(logScrollPane, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, panel);
        splitPane.setDividerLocation(300);
        frame.add(splitPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // добавление систем в дерево
        systems.forEach(system -> addSystemToTree(system));

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // получаем ближайшую строку
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    TreePath path = tree.getPathForRow(row);
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (selectedNode.getUserObject() instanceof HomeEngineeringSystem) {
                        //
                        showContextMenu(e.getX(), e.getY(), (HomeEngineeringSystem) selectedNode.getUserObject());
                    }
                }
            }
        });


//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleAtFixedRate(this::updateSystemData, 0, 4, TimeUnit.SECONDS);
    }

    protected void addSystemToTree(HomeEngineeringSystem system) {
        DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode(system);
        for (Sensor<?> sensor : system.getSensors()) {
            DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensor);
            systemNode.add(sensorNode);
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.add(systemNode);
        treeModel.reload();
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    private void updateAllSystemData() {
        TreePath[] expandedPaths = getExpandedPaths(tree);
//        System.out.println(systems);
        systems.forEach(system -> this.updateSystemData(system));
        systems.forEach(system -> system.checkAnyAnomaly());
        SwingUtilities.invokeLater(() -> {
            treeModel.reload();
            restoreExpandedPaths(tree, expandedPaths);
        });
    }

    private TreePath[] getExpandedPaths(JTree tree) {
        List<TreePath> expandedPaths = new ArrayList<>();
        for (int i = 0; i < tree.getRowCount(); i++) {
            if (tree.isExpanded(i)) {
                expandedPaths.add(tree.getPathForRow(i));
            }
        }
        return expandedPaths.toArray(new TreePath[0]);
    }

    private void restoreExpandedPaths(JTree tree, TreePath[] paths) {
        for (TreePath path : paths) {
            tree.expandPath(path);
        }
    }

    public void updateSystemData(HomeEngineeringSystem system) {
        system.generateData();
        system.getSensors().forEach(sensor -> system.writeToTempLogFile(sensor.toString()));
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration<?> enumeration = root.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject() instanceof HomeEngineeringSystem) {
                HomeEngineeringSystem currentSystem = (HomeEngineeringSystem) node.getUserObject();
                if (currentSystem.equals(system)) {
                    for (int i = 0; i < node.getChildCount(); i++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                        Sensor<?> sensor = system.getSensors().get(i);
                        childNode.setUserObject(sensor);

                        if (sensor.isDeviceOff()) {
                            logDisabledSensor(system, sensor);
                            notifyUser(system, sensor, false); // Уведомление об отключении
                        } else {
                            if (sensor.isThresholdExceeded()) {
                                logAnomaly(system, sensor);
                                notifyUser(system, sensor, true); // Уведомление об аномалии
                            }
                        }
                    }
                }
            }
        }
    }


    private void logAnomaly(HomeEngineeringSystem system, Sensor<?> sensor) {
        if (sensor.isDeviceOff()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            logArea.append("В системе '" + system.getName() + "' произошла аномалия в датчике '" + sensor.getType() + "'  " + sensor.getFormattedLastUpdateTime() + "'\n");
        });
    }

    private void logDisabledSensor(HomeEngineeringSystem system, Sensor<?> sensor) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("В системе '" + system.getName() + "' произошло отключение датчика '" + sensor.getType() + "'  " + sensor.getFormattedLastUpdateTime() + "\n");
        });
    }

    private void notifyUser(HomeEngineeringSystem system, Sensor<?> sensor, boolean isAnomaly) {
        String message = isAnomaly ? "произошла аномалия" : "произошло отключение";
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "В системе '" + system.getName() + "' " + message + " в датчике '" + sensor.getType() + "'", "Обнаружена " + message, JOptionPane.WARNING_MESSAGE);
        });
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void showContextMenu(int x, int y, HomeEngineeringSystem system) {
        // Создаем новое контекстное меню
        JPopupMenu contextMenu = new JPopupMenu();
        // Создаем пункт меню для генерации отчета
        JMenuItem generateReportItem = new JMenuItem("Сгенерировать отчет");
        // Добавляем обработчик событий для клика по пункту меню
        generateReportItem.addActionListener(e -> {
            // Создаем файл-выборщик для выбора директории сохранения отчета
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Сохранить отчет как");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // Отображаем диалог выбора директории и проверяем выбор пользователя
            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Получаем выбранную директорию
                File directoryToSave = fileChooser.getSelectedFile();
                // Формируем полный путь к файлу для сохранения отчета
                String filePath = directoryToSave.getAbsolutePath() + File.separator + system.getName() + "Отчет";
                // Сохраняем отчет в файл
                system.saveReportToExcel(filePath);
                // Отображаем сообщение об успешном сохранении
                JOptionPane.showMessageDialog(frame, "Отчет сохранен в " + filePath, "Отчет сохранен", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // Добавляем пункт меню в контекстное меню
        contextMenu.add(generateReportItem);
        // Отображаем контекстное меню в месте клика мыши
        contextMenu.show(tree, x, y);
    }


    // при вызове метода treeModel.reload() ререндериться
    private static class SensorTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof Sensor<?>) {
                    Sensor<?> sensor = (Sensor<?>) userObject;
                    if (sensor.isDeviceOff()) {
                        c.setForeground(Color.GRAY);
                        setText(sensor.toString());
                    } else if (sensor.isThresholdExceeded()) {
                        c.setForeground(Color.RED);
                        setText(sensor.toString());
                    } else {
                        c.setForeground(Color.BLACK);
                        setText(sensor.toString());
                    }
                }
            }
            return c;
        }
    }
}
