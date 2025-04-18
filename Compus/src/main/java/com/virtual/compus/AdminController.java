package com.virtual.compus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminController {@FXML
private StackPane contentArea;

    @FXML private PieChart satisfactionChart;
    @FXML private BarChart<String, Number> occupationChart;
    @FXML private ProgressBar wifiUsage;
    @FXML private GridPane dashboardPane;
    @FXML private GridPane studentsPane;


    private Map<String, Parent> views = new HashMap<>();

    @FXML
    public void initialize() {
        loadViews();
        updateCharts();
    }

    private void loadViews() {

    }

    public void showDashboard() {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        dashboardPane.setVisible(true);
    }

    public void showStudents() {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        studentsPane.setVisible(true);
        //switchView("students");
        // Logique de chargement des étudiants depuis la BDD
    }

    private void updateCharts() {
        // Satisfaction des étudiants
        PieChart.Data slice1 = new PieChart.Data("Satisfaits", 65);
        PieChart.Data slice2 = new PieChart.Data("Neutres", 25);
        PieChart.Data slice3 = new PieChart.Data("Insatisfaits", 10);
        satisfactionChart.getData().addAll(slice1, slice2, slice3);

        // Occupation des bâtiments
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Bibliothèque", 80));
        series.getData().add(new XYChart.Data<>("Cafétéria", 45));
        occupationChart.getData().add(series);
    }

    @FXML
    private void exportPDF() {
        // Logique d'export PDF avec Apache PDFBox
    }

    @FXML
    private void exportExcel() {
        // Logique d'export Excel avec Apache POI
    }

    @FXML
    private void performBackup() {
        try {
            ProcessBuilder pb = new ProcessBuilder("mysqldump", "-u", "root", "compus");
            pb.redirectOutput(new File("backup.sql"));
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(ActionEvent actionEvent) {
    }

    public void deleteStudent(ActionEvent actionEvent) {
    }

    public void addProfessor(ActionEvent actionEvent) {
    }

    public void toggleAvailability(ActionEvent actionEvent) {
    }

    public void addBuilding(ActionEvent actionEvent) {
    }

    public void restoreBackup(ActionEvent actionEvent) {
    }

    public void showProfessors(ActionEvent actionEvent) {
    }

    public void showBuildings(ActionEvent actionEvent) {
    }

    public void showBackup(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
    }
}