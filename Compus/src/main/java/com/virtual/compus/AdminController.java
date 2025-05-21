package com.virtual.compus;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.virtual.compus.AddEtudiantController;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;


public class AdminController {

    @FXML private StackPane contentArea;
    @FXML private PieChart satisfactionChart;
    @FXML private BarChart<String, Number> occupationChart;
    @FXML private ProgressBar wifiUsage;
    @FXML private ProgressBar electriciteUsage;
    @FXML private Label wifiUsageLabel;
    @FXML private Label electriciteUsageLabel;
    @FXML private ProgressBar eauUsage;
    @FXML private Label eauUsageLabel;
    @FXML private ProgressBar espaceUsage;
    @FXML private Label espaceUsageLabel;

    @FXML private GridPane dashboardPane;
    @FXML private GridPane studentsPane;
    @FXML private GridPane professorsPane;
    @FXML private GridPane buildingsPane;
    @FXML private GridPane backupPane;

    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> nomColumn;
    @FXML private TableColumn<Etudiant, String> filiereColumn;
    @FXML private TableColumn<Etudiant, Number> satisfactionColumn;
    @FXML private TableColumn<Etudiant, Number> heuresCoursColumn;
    @FXML private TextField searchStudentField;

    // Professeurs
    @FXML private TableView<Professeur> professorsTable;
    @FXML private TableColumn<Professeur, String> nomProfColumn;
    @FXML private TableColumn<Professeur, String> matiereColumn;
    @FXML private TableColumn<Professeur, Boolean> disponibleColumn;
    @FXML private TextField searchProfesseurField;
    @FXML private PieChart profSatisfactionChart;



    //batiment
    @FXML private TableView<Batiment> batimentsTable;
    @FXML private TableColumn<Batiment, String> nomBatimentColumn;
    @FXML private TableColumn<Batiment, String> typeBatimentColumn;
    @FXML private TableColumn<Batiment, Integer> capaciteBatimentColumn;
    @FXML private TableColumn<Batiment, Double> consommationBatimentColumn;
    @FXML private TableColumn<Batiment, Double> impactSatisfactionBatimentColumn;
    @FXML private TableColumn<Batiment, Double> electriciteBatimentColumn;
    @FXML private TableColumn<Batiment, Double> eauBatimentColumn;
    @FXML private TableColumn<Batiment, Double> espaceBatimentColumn;
    @FXML private TableColumn<Batiment, Double> wifiBatimentColumn;

    @FXML private Button addBatimentButton;
    @FXML private Button deleteBatimentButton;
    @FXML private Button modifyBatimentButton;
    @FXML private TextField searchBatimentField;

    @FXML private Button optimizeResourcesButton;
    @FXML private TableColumn<Batiment, Integer> occupationActuelleBatimentColumn;

    private Map<String, Parent> views = new HashMap<>();

    private ObservableList<Etudiant> etudiantsList = FXCollections.observableArrayList();
    private FilteredList<Etudiant> filteredEtudiantsList;

    private ObservableList<Professeur> professeursList = FXCollections.observableArrayList();
    private FilteredList<Professeur> filteredProfesseursList;

    private ObservableList<Batiment> batimentsList = FXCollections.observableArrayList();
    private FilteredList<Batiment> filteredBatimentsList; // For building search

    @FXML private Button exportPDFButton;  // Nouveau bouton PDF
    @FXML private Button exportExcelButton;

    private SimulationCampus simulation;
    @FXML private Button simulateCycleButton;
    @FXML private Label lastEventLabel;

    @FXML private Button backupButton;
    @FXML private Button restoreButton;

    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ou ton mot de passe


    public static Connection getConnection() throws SQLException {
        System.out.println("test cnx");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Variables pour suivre les ressources disponibles
    private double availableWifi;
    private double availableElectricite;
    private double availableEau;
    private double availableEspace;

    @FXML
    public void initialize() {

        filteredEtudiantsList = new FilteredList<>(etudiantsList, p -> true);
        studentsTable.setItems(filteredEtudiantsList);

        searchStudentField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEtudiantsList.setPredicate(etudiant -> {
                // Si le champ de recherche est vide, afficher tous les étudiants
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                // Convertir le texte de recherche en minuscules
                String lowerCaseFilter = newValue.toLowerCase();

                // Vérifier si le nom ou la filière contient le texte de recherche
                if (etudiant.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (etudiant.getFiliere().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                // Vous pouvez ajouter d'autres critères, par exemple :
                // if (String.valueOf(etudiant.getSatisfaction()).contains(lowerCaseFilter)) {
                //     return true;
                // }
                // if (String.valueOf(etudiant.getHeuresCours()).contains(lowerCaseFilter)) {
                //     return true;
                // }

                return false; // Ne correspond à aucun critère
            });
        });

        // Charger les données initiales


        // Initialisation pour les professeurs
        filteredProfesseursList = new FilteredList<>(professeursList, p -> true);
        professorsTable.setItems(filteredProfesseursList);

        searchProfesseurField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProfesseursList.setPredicate(professeur -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (professeur.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (professeur.getMatiere().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });


        //  Initialisation pour les Batiments
        filteredBatimentsList = new FilteredList<>(batimentsList, p -> true);
        batimentsTable.setItems(filteredBatimentsList);


        electriciteBatimentColumn.setCellValueFactory(cellData -> {
            Batiment batiment = cellData.getValue();
            Double consommation = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
            return new javafx.beans.property.SimpleDoubleProperty(consommation).asObject();
        });
        eauBatimentColumn.setCellValueFactory(cellData -> {
            Batiment batiment = cellData.getValue();
            Double consommation = batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0);
            return new javafx.beans.property.SimpleDoubleProperty(consommation).asObject();
        });
        espaceBatimentColumn.setCellValueFactory(cellData -> {
            Batiment batiment = cellData.getValue();
            Double consommation = batiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0);
            return new javafx.beans.property.SimpleDoubleProperty(consommation).asObject();
        });
        wifiBatimentColumn.setCellValueFactory(cellData -> {
            Batiment batiment = cellData.getValue();
            Double consommation = batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
            return new javafx.beans.property.SimpleDoubleProperty(consommation).asObject();
        });

        impactSatisfactionBatimentColumn.setCellValueFactory(new PropertyValueFactory<>("impactSatisfaction"));
        occupationActuelleBatimentColumn.setCellValueFactory(new PropertyValueFactory<>("occupationActuelle"));

        searchBatimentField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBatimentsList.setPredicate(batiment -> {
                // If the search field is empty, show all buildings
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                // Convert search text to lowercase
                String lowerCaseFilter = newValue.toLowerCase();

                // Check if the name or type contains the search text
                if (batiment.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (batiment.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                // Optional: Add more search criteria
                // if (String.valueOf(batiment.getCapacite()).contains(lowerCaseFilter)) {
                //     return true;
                // }
                // if (String.valueOf(batiment.getImpactSatisfaction()).contains(lowerCaseFilter)) {
                //     return true;
                // }

                return false; // Does not match any criteria
            });
        });



        simulation = new SimulationCampus(batimentsList, etudiantsList, professeursList);


        // Initialiser les ressources disponibles à leurs maximums
        availableWifi = Ressource.WIFI.getMaxConsommation();
        availableElectricite = Ressource.ELECTRICITE.getMaxConsommation();
        availableEau = Ressource.EAU.getMaxConsommation();
        availableEspace = Ressource.ESPACE.getMaxConsommation();

        loadStudents();

        loadProfesseurs();

        loadBatiments();

        loadViews();


        updateCharts();
        updateProfSatisfactionChart();
        updateBcharts();


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

    public void showProfessors(ActionEvent actionEvent) {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        professorsPane.setVisible(true);
    }

    public void showBuildings(ActionEvent actionEvent) {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        buildingsPane.setVisible(true);
    }

    public void showBackup(ActionEvent actionEvent) {
        contentArea.getChildren().forEach(node -> node.setVisible(false));
        backupPane.setVisible(true);
    }



    private void updateCharts() {

        // Réinitialiser les données du PieChart
        satisfactionChart.getData().clear();

        // Compter les étudiants par catégorie
        int satisfaits = 0;
        int neutres = 0;
        int insatisfaits = 0;
        int totalEtudiants = etudiantsList.size();

        for (Etudiant etudiant : etudiantsList) {
            double satisfaction = etudiant.getSatisfaction();
            if (satisfaction >= 70) {
                satisfaits++;
            } else if (satisfaction >= 50) {
                neutres++;
            } else {
                insatisfaits++;
            }
        }

        // Calculer les pourcentages
        double satisfaitsPourcentage = totalEtudiants > 0 ? (satisfaits * 100.0 / totalEtudiants) : 0;
        double neutresPourcentage = totalEtudiants > 0 ? (neutres * 100.0 / totalEtudiants) : 0;
        double insatisfaitsPourcentage = totalEtudiants > 0 ? (insatisfaits * 100.0 / totalEtudiants) : 0;

        // Ajouter les données au PieChart
        PieChart.Data slice1 = new PieChart.Data(String.format("Satisfaits (%.1f%%)", satisfaitsPourcentage), satisfaits);
        PieChart.Data slice2 = new PieChart.Data(String.format("Neutres (%.1f%%)", neutresPourcentage), neutres);
        PieChart.Data slice3 = new PieChart.Data(String.format("Insatisfaits (%.1f%%)", insatisfaitsPourcentage), insatisfaits);

        satisfactionChart.getData().addAll(slice1, slice2, slice3);



        double wifiMax = Ressource.WIFI.getMaxConsommation();
        double electriciteMax = Ressource.ELECTRICITE.getMaxConsommation();
        double eauMax = Ressource.EAU.getMaxConsommation();
        double espaceMax = Ressource.ESPACE.getMaxConsommation();

        // Calculer les pourcentages restants et plafonner entre 0 et 1 (0% à 100%)
        double wifiUsagePercent = wifiMax > 0 ? Math.min(Math.max(availableWifi / wifiMax, 0.0), 1.0) : 0;
        double electriciteUsagePercent = electriciteMax > 0 ? Math.min(Math.max(availableElectricite / electriciteMax, 0.0), 1.0) : 0;
        double eauUsagePercent = eauMax > 0 ? Math.min(Math.max(availableEau / eauMax, 0.0), 1.0) : 0;
        double espaceUsagePercent = espaceMax > 0 ? Math.min(Math.max(availableEspace / espaceMax, 0.0), 1.0) : 0;

        wifiUsage.setProgress(wifiUsagePercent);
        wifiUsageLabel.setText(String.format("WiFi: %.0f%%", wifiUsagePercent * 100));
        electriciteUsage.setProgress(electriciteUsagePercent);
        electriciteUsageLabel.setText(String.format("Électricité: %.0f%%", electriciteUsagePercent * 100));
        eauUsage.setProgress(eauUsagePercent);
        eauUsageLabel.setText(String.format("Eau: %.0f%%", eauUsagePercent * 100));
        espaceUsage.setProgress(espaceUsagePercent);
        espaceUsageLabel.setText(String.format("Espace: %.0f%%", espaceUsagePercent * 100));

    }

    private void updateProfSatisfactionChart() {
        profSatisfactionChart.getData().clear();
        int satisfaits = 0;
        int neutres = 0;
        int insatisfaits = 0;
        int totalProfesseurs = professeursList.size();
        for (Professeur professeur : professeursList) {
            double satisfaction = professeur.getSatisfaction();
            if (satisfaction >= 70) {
                satisfaits++;
            } else if (satisfaction >= 50) {
                neutres++;
            } else {
                insatisfaits++;
            }
        }
        double satisfaitsPourcentage = totalProfesseurs > 0 ? (satisfaits * 100.0 / totalProfesseurs) : 0;
        double neutresPourcentage = totalProfesseurs > 0 ? (neutres * 100.0 / totalProfesseurs) : 0;
        double insatisfaitsPourcentage = totalProfesseurs > 0 ? (insatisfaits * 100.0 / totalProfesseurs) : 0;
        PieChart.Data slice1 = new PieChart.Data(String.format("Satisfaits (%.1f%%)", satisfaitsPourcentage), satisfaits);
        PieChart.Data slice2 = new PieChart.Data(String.format("Neutres (%.1f%%)", neutresPourcentage), neutres);
        PieChart.Data slice3 = new PieChart.Data(String.format("Insatisfaits (%.1f%%)", insatisfaitsPourcentage), insatisfaits);
        profSatisfactionChart.getData().addAll(slice1, slice2, slice3);
    }

    private  void  updateBcharts(){
        occupationChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Batiment batiment : batimentsList) {
            double occupation = (batiment.getCapacite());
            series.getData().add(new XYChart.Data<>(batiment.getNom(), occupation));
        }
        occupationChart.getData().add(series);




    }



    @FXML
    private void exportPDFB() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Statistiques des Bâtiments - Campus Virtuel")
                        .setBold().setFontSize(16));

                // Créer un tableau avec 8 colonnes
                Table table = new Table(8);
                table.addHeaderCell("Nom");
                table.addHeaderCell("Type");
                table.addHeaderCell("Capacité");
                table.addHeaderCell("Occupation Actuelle");
                table.addHeaderCell("Électricité (kWh)");
                table.addHeaderCell("Eau (litres)");
                table.addHeaderCell("Espace (m²)");
                table.addHeaderCell("WiFi (Mbps)");

                // Remplir le tableau avec les données des bâtiments
                for (Batiment batiment : batimentsList) {
                    table.addCell(batiment.getNom());
                    table.addCell(batiment.getType());
                    table.addCell(String.valueOf(batiment.getCapacite()));
                    table.addCell(String.valueOf(batiment.getOccupationActuelle()));
                    table.addCell(String.valueOf(batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0)));
                    table.addCell(String.valueOf(batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0)));
                    table.addCell(String.valueOf(batiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0)));
                    table.addCell(String.valueOf(batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0)));
                }

                document.add(table);
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en PDF avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportExcelB() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Statistiques Bâtiments");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Nom");
                headerRow.createCell(1).setCellValue("Type");
                headerRow.createCell(2).setCellValue("Capacité");
                headerRow.createCell(3).setCellValue("Occupation Actuelle");
                headerRow.createCell(4).setCellValue("Électricité (kWh)");
                headerRow.createCell(5).setCellValue("Eau (litres)");
                headerRow.createCell(6).setCellValue("Espace (m²)");
                headerRow.createCell(7).setCellValue("WiFi (Mbps)");

                // Remplir les données
                int rowNum = 1;
                for (Batiment batiment : batimentsList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(batiment.getNom());
                    row.createCell(1).setCellValue(batiment.getType());
                    row.createCell(2).setCellValue(batiment.getCapacite());
                    row.createCell(3).setCellValue(batiment.getOccupationActuelle());
                    row.createCell(4).setCellValue(batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0));
                    row.createCell(5).setCellValue(batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0));
                    row.createCell(6).setCellValue(batiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0));
                    row.createCell(7).setCellValue(batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0));
                }

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 8; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire dans le fichier
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en Excel avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en Excel : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportPDFS() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Statistiques des Etudiants - Campus Virtuel")
                        .setBold().setFontSize(16));

                // Créer un tableau avec 4 colonnes
                Table table = new Table(4);
                table.addHeaderCell("Nom");
                table.addHeaderCell("Filiere");
                table.addHeaderCell("Satisfaction");
                table.addHeaderCell("Heures_cours");


                // Remplir le tableau avec les données des bâtiments
                for (Etudiant etudiant : etudiantsList) {
                    table.addCell(etudiant.getNom());
                    table.addCell(etudiant.getFiliere());
                    table.addCell(String.valueOf(etudiant.getSatisfaction()));
                    table.addCell(String.valueOf(etudiant.getHeuresCours()));
                }

                document.add(table);
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en PDF avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportExcelS() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Statistiques Etudiants");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Nom");
                headerRow.createCell(1).setCellValue("Filiere");
                headerRow.createCell(2).setCellValue("Satisfaction");
                headerRow.createCell(3).setCellValue("Heures_cours");


                // Remplir les données
                int rowNum = 1;
                for (Etudiant etudiant : etudiantsList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(etudiant.getNom());
                    row.createCell(1).setCellValue(etudiant.getFiliere());
                    row.createCell(2).setCellValue(etudiant.getSatisfaction());
                    row.createCell(3).setCellValue(etudiant.getHeuresCours());
                }

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 8; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire dans le fichier
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en Excel avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en Excel : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportPDFP() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Statistiques des Professeur - Campus Virtuel")
                        .setBold().setFontSize(16));

                // Créer un tableau avec 4 colonnes
                Table table = new Table(4);
                table.addHeaderCell("Nom");
                table.addHeaderCell("Matiere");
                table.addHeaderCell("Disponibilite");
                table.addHeaderCell("satisfaction");


                // Remplir le tableau avec les données des bâtiments
                for (Professeur professeur : professeursList) {
                    table.addCell(professeur.getNom());
                    table.addCell(professeur.getMatiere());
                    table.addCell(String.valueOf(professeur.getDisponible()));
                    table.addCell(String.valueOf(professeur.getSatisfaction()));
                }

                document.add(table);
                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en PDF avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportExcelP() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Statistiques Bâtiments");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Nom");
                headerRow.createCell(1).setCellValue("Matiere");
                headerRow.createCell(2).setCellValue("Disponibilite");
                headerRow.createCell(3).setCellValue("satisfaction");

                // Remplir les données
                int rowNum = 1;
                for (Professeur professeur : professeursList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(professeur.getNom());
                    row.createCell(1).setCellValue(professeur.getMatiere());
                    row.createCell(2).setCellValue(professeur.getSatisfaction());
                    row.createCell(3).setCellValue(professeur.getDisponible());
                    }

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 8; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire dans le fichier
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statistiques exportées en Excel avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation en Excel : " + e.getMessage());
            }
        }
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
        // Récupérer l'étudiant sélectionné
        Etudiant selectedEtudiant = studentsTable.getSelectionModel().getSelectedItem();

        // Vérifier si un étudiant est sélectionné
        if (selectedEtudiant == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun étudiant sélectionné", "Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        // Supprimer l'étudiant de la base de données
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM personnes WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedEtudiant.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Supprimer l'étudiant de la liste
                etudiantsList.remove(selectedEtudiant);
                loadStudents();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Étudiant supprimé avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun étudiant trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addProfessor(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virtual/compus/ajouterP.fxml"));
            Parent root = loader.load();

            AddProfesseurController addController = loader.getController();
            addController.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Professeur");
            stage.setScene(new Scene(root));
            loadProfesseurs();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addBuilding(ActionEvent actionEvent) {
    }

    public void restoreBackup(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
    }


    public void openAddStudentWindow(ActionEvent actionEvent) {
        try {
            // Charger le fichier ajouterE.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virtual/compus/ajouterE.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur et passer la référence à AdminMainController
            AddEtudiantController addController = loader.getController();
            addController.setParentController(this);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Étudiant");
            stage.setScene(new Scene(root));
            loadStudents();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {

        System.out.println("test9");

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void loadStudents() {
        etudiantsList.clear();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id,nom, filiere, satisfaction, heures_cours FROM personnes WHERE type='ETUDIANT';")) {

            while (rs.next()) {
                int id =rs.getInt("id");
                String nom = rs.getString("nom");
                String filiere = rs.getString("filiere");
                double satisfaction = rs.getDouble("satisfaction"); // Ajustez si c'est une chaîne
                int heuresCours = rs.getInt("heures_cours");

                etudiantsList.add(new Etudiant(id,nom, filiere, satisfaction, heuresCours));
            }
            updateCharts();
        } catch (SQLException e) {
            e.printStackTrace();
            // Vous pouvez ajouter une alerte ici
        }

        System.out.println("Students:");
        etudiantsList.forEach(etudiant -> System.out.println(etudiant.getNom()));

    }

    public void loadProfesseurs() {
        professeursList.clear();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom, matiere, disponibilite,satisfaction FROM personnes WHERE type='PROFESSEUR';")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String matiere = rs.getString("matiere");
                boolean disponible = rs.getBoolean("disponibilite");
                int satisfaction = rs.getInt("satisfaction");
                professeursList.add(new Professeur(id, nom, matiere, disponible,satisfaction));
            }
            updateCharts();
            updateProfSatisfactionChart();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des professeurs: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Professeurs:");
        etudiantsList.forEach(Professeur -> System.out.println(Professeur.getNom()));

    }

    public void loadBatiments() {
        batimentsList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom, type, capacite, impact_satisfaction,occupation_actuelle FROM batiments")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String type = rs.getString("type");
                int capacite = rs.getInt("capacite");
                int occupationActuelle = rs.getInt("occupation_actuelle");
                double impactSatisfaction = rs.getDouble("impact_satisfaction");

                // Create the appropriate Batiment subclass
                Batiment batiment;
                switch (type) {
                    case "Salle de Cours":
                        batiment = new SalleCours(id, nom, type, capacite, impactSatisfaction,occupationActuelle);
                        break;
                    case "Bibliothèque":
                        batiment = new Bibliotheque(id, nom, type, capacite, impactSatisfaction,occupationActuelle);
                        break;
                    case "Cafétéria":
                        batiment = new Cafeteria(id, nom, type, capacite, impactSatisfaction,occupationActuelle);
                        break;
                    case "Laboratoire":
                        batiment = new Laboratoire(id, nom, type, capacite, impactSatisfaction,occupationActuelle);
                        break;
                    default:
                        continue;
                }

                try (PreparedStatement ressourceStmt = conn.prepareStatement("SELECT ressource, consommation FROM batiment_ressources WHERE batiment_id = ?")) {
                    ressourceStmt.setInt(1, id);
                    ResultSet ressourceRs = ressourceStmt.executeQuery();
                    while (ressourceRs.next()) {
                        Ressource ressource = Ressource.valueOf(ressourceRs.getString("ressource"));
                        double consommation = ressourceRs.getDouble("consommation");
                        batiment.setConsommationRessource(ressource, consommation);

                        // Soustraire les consommations des ressources disponibles
                        if (ressource == Ressource.WIFI) {
                            availableWifi -= consommation;
                        } else if (ressource == Ressource.ELECTRICITE) {
                            availableElectricite -= consommation;
                        } else if (ressource == Ressource.EAU) {
                            availableEau -= consommation;
                        } else if (ressource == Ressource.ESPACE) {
                            availableEspace -= consommation;
                        }
                    }
                }
                batimentsList.add(batiment);
            }
            updateCharts();
            updateBcharts();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des bâtiments: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProf(ActionEvent actionEvent) {
        // Récupérer le professeur sélectionné
        Professeur selectedProfesseur = professorsTable.getSelectionModel().getSelectedItem();

        // Vérifier si un professeur est sélectionné
        if (selectedProfesseur == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun professeur sélectionné", "Veuillez sélectionner un professeur à supprimer.");
            return;
        }

        // Supprimer le sprofesseur de la base de données
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM personnes WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedProfesseur.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Supprimer le professeur de la liste
                professeursList.remove(selectedProfesseur);
                loadProfesseurs();
                updateProfSatisfactionChart();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Professeur supprimé avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun Professeur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierP(ActionEvent event) {

        Professeur selectedProfesseur = professorsTable.getSelectionModel().getSelectedItem();
        if (selectedProfesseur == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun professeur sélectionné", "Veuillez sélectionner un professeur à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virtual/compus/modifierP.fxml"));
            Parent root = loader.load();
            ModifierProfesseurController modifyController = loader.getController();
            modifyController.setParentController(this);
            modifyController.setProfesseur(selectedProfesseur);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Professeur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void modifierS(ActionEvent actionEvent) {
        Etudiant selectedEtudiant = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedEtudiant == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun étudiant sélectionné", "Veuillez sélectionner un étudiant à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virtual/compus/modifierS.fxml"));
            Parent root = loader.load();
            ModifierEtudiantController modifyController = loader.getController();
            modifyController.setParentController(this);
            modifyController.setEtudiant(selectedEtudiant);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Étudiant");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    public void addBatiment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virtual/compus/AddBatiment.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter un Bâtiment");
            // Récupérer le contrôleur et passer les ressources disponibles
            AddBatimentController controller = loader.getController();
            controller.setAvailableResources(availableWifi, availableElectricite, availableEau, availableEspace);


            stage.showAndWait();
            loadBatiments();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage());
        }
    }

    @FXML
    public void deleteBatiment() {
        Batiment selectedBatiment = batimentsTable.getSelectionModel().getSelectedItem();
        if (selectedBatiment != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Restaurer les ressources consommées par ce bâtiment
                double wifiConsumed = selectedBatiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
                double electriciteConsumed = selectedBatiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
                double eauConsumed = selectedBatiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0);
                double espaceConsumed = selectedBatiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0);

                availableWifi += wifiConsumed;
                availableElectricite += electriciteConsumed;
                availableEau += eauConsumed;
                availableEspace += espaceConsumed;

                // Supprimer de la base de données
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM batiment_ressources WHERE batiment_id = ?")) {
                    stmt.setInt(1, selectedBatiment.getId());
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM batiments WHERE id = ?")) {
                    stmt.setInt(1, selectedBatiment.getId());
                    stmt.executeUpdate();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            loadBatiments();
            updateCharts();
            updateBcharts();
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune Sélection", "Veuillez sélectionner un bâtiment à supprimer.");
        }
    }

    @FXML
    public void modifyBatiment() {
        Batiment selected = batimentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un bâtiment à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyBatiment.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            ModifyBatimentController controller = loader.getController();
            controller.setBatiment(selected);
            stage.setTitle("Modifier un Bâtiment");
            stage.showAndWait();
            loadBatiments();
        } catch (IOException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Erreur lors de la modification du bâtiment: " + e.getMessage());
        }
    }


    @FXML
    private void optimizeResources() {
        // Vérifier si une optimisation est nécessaire
        boolean optimizationNeeded = false;
        if (availableWifi < 0 || availableElectricite < 0 || availableEau < 0 || availableEspace < 0) {
            optimizationNeeded = true;
        }

        if (!optimizationNeeded) {
            showAlert(Alert.AlertType.INFORMATION, "Optimisation", "Les ressources sont suffisantes, aucune optimisation nécessaire.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Calculer les consommations totales actuelles pour chaque ressource
            double totalWifiConsumed = 0;
            double totalElectriciteConsumed = 0;
            double totalEauConsumed = 0;
            double totalEspaceConsumed = 0;

            for (Batiment batiment : batimentsList) {
                totalWifiConsumed += batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
                totalElectriciteConsumed += batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
                totalEauConsumed += batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0);
                totalEspaceConsumed += batiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0);
            }

            // Calculer les facteurs de réduction pour ramener les ressources disponibles à 0 ou plus
            double wifiReductionFactor = 1.0;
            double electriciteReductionFactor = 1.0;
            double eauReductionFactor = 1.0;
            double espaceReductionFactor = 1.0;

            double wifiMax = Ressource.WIFI.getMaxConsommation();
            double electriciteMax = Ressource.ELECTRICITE.getMaxConsommation();
            double eauMax = Ressource.EAU.getMaxConsommation();
            double espaceMax = Ressource.ESPACE.getMaxConsommation();

            if (availableWifi < 0) {
                // Objectif : Réduire totalWifiConsumed pour que availableWifi >= 0
                double targetWifiConsumed = wifiMax; // On veut que availableWifi soit au moins 0
                wifiReductionFactor = targetWifiConsumed / totalWifiConsumed;
            }
            if (availableElectricite < 0) {
                double targetElectriciteConsumed = electriciteMax;
                electriciteReductionFactor = targetElectriciteConsumed / totalElectriciteConsumed;
            }
            if (availableEau < 0) {
                double targetEauConsumed = eauMax;
                eauReductionFactor = targetEauConsumed / totalEauConsumed;
            }
            if (availableEspace < 0) {
                double targetEspaceConsumed = espaceMax;
                espaceReductionFactor = targetEspaceConsumed / totalEspaceConsumed;
            }

            // Appliquer les réductions et ajuster l'impactSatisfaction
            for (Batiment batiment : batimentsList) {
                double wifi = batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
                double electricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
                double eau = batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0);
                double espace = batiment.getConsommationRessources().getOrDefault(Ressource.ESPACE, 0.0);

                // Appliquer les facteurs de réduction
                double newWifi = wifi * wifiReductionFactor;
                double newElectricite = electricite * electriciteReductionFactor;
                double newEau = eau * eauReductionFactor;
                double newEspace = espace * espaceReductionFactor;

                // Calculer la réduction totale pour ajuster l'impactSatisfaction
                double totalReduction = 0;
                if (wifi > 0) totalReduction += (wifi - newWifi) / wifi;
                if (electricite > 0) totalReduction += (electricite - newElectricite) / electricite;
                if (eau > 0) totalReduction += (eau - newEau) / eau;
                if (espace > 0) totalReduction += (espace - newEspace) / espace;

                // Ajuster l'impactSatisfaction (réduction de 0.5 par 10% de réduction moyenne)
                double satisfactionAdjustment = (totalReduction / 4) * 0.5; // Divisé par 4 car 4 ressources
                double newImpactSatisfaction = batiment.getImpactSatisfaction() - satisfactionAdjustment;
                batiment.setImpactSatisfaction(Math.max(0, newImpactSatisfaction)); // Ne pas descendre en dessous de 0

                // Mettre à jour les consommations
                batiment.setConsommationRessource(Ressource.WIFI, newWifi);
                batiment.setConsommationRessource(Ressource.ELECTRICITE, newElectricite);
                batiment.setConsommationRessource(Ressource.EAU, newEau);
                batiment.setConsommationRessource(Ressource.ESPACE, newEspace);
            }

            // Sauvegarder les changements dans la base de données
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE batiments SET impact_satisfaction = ? WHERE id = ?")) {
                for (Batiment batiment : batimentsList) {
                    updateStmt.setDouble(1, batiment.getImpactSatisfaction());
                    updateStmt.setInt(2, batiment.getId());
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM batiment_ressources WHERE batiment_id = ?");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO batiment_ressources (batiment_id, ressource, consommation) VALUES (?, ?, ?)")) {
                for (Batiment batiment : batimentsList) {
                    deleteStmt.setInt(1, batiment.getId());
                    deleteStmt.addBatch();

                    for (java.util.Map.Entry<Ressource, Double> entry : batiment.getConsommationRessources().entrySet()) {
                        if (entry.getValue() > 0) {
                            insertStmt.setInt(1, batiment.getId());
                            insertStmt.setString(2, entry.getKey().toString());
                            insertStmt.setDouble(3, entry.getValue());
                            insertStmt.addBatch();
                        }
                    }
                }
                deleteStmt.executeBatch();
                insertStmt.executeBatch();
            }

            conn.commit();

            // Recharger les données et mettre à jour les graphiques
            loadBatiments();
            updateCharts();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Ressources optimisées avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'optimisation des ressources: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void simulateCycle() {
        simulation.simulerCycle();
        simulation.saveToDatabase();
        loadStudents(); // Refresh student data
        loadProfesseurs(); // Refresh professor data
        loadBatiments(); // Refresh building data
        updateCharts(); // Update charts with new satisfaction values
        updateBcharts();
        String eventMessage = simulation.getLastEventMessage();
        lastEventLabel.setText(eventMessage);
        showAlert(Alert.AlertType.INFORMATION, "Simulation", "Cycle de simulation terminé.");
    }



    @FXML
    private void backupData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder les données");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
        File file = fileChooser.showSaveDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try {
                JSONObject backup = new JSONObject();

                // Sauvegarde des bâtiments
                JSONArray batimentsArray = new JSONArray();
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM batiments")) {
                    while (rs.next()) {
                        JSONObject batiment = new JSONObject();
                        batiment.put("id", rs.getInt("id"));
                        batiment.put("nom", rs.getString("nom"));
                        batiment.put("type", rs.getString("type"));
                        batiment.put("capacite", rs.getInt("capacite"));
                        batiment.put("occupation_actuelle", rs.getInt("occupation_actuelle"));
                        batiment.put("impact_satisfaction", rs.getDouble("impact_satisfaction"));

                        // Ressources du bâtiment
                        JSONArray ressourcesArray = new JSONArray();
                        try (PreparedStatement ressourceStmt = conn.prepareStatement("SELECT ressource, consommation FROM batiment_ressources WHERE batiment_id = ?")) {
                            ressourceStmt.setInt(1, rs.getInt("id"));
                            ResultSet ressourceRs = ressourceStmt.executeQuery();
                            while (ressourceRs.next()) {
                                JSONObject ressource = new JSONObject();
                                ressource.put("ressource", ressourceRs.getString("ressource"));
                                ressource.put("consommation", ressourceRs.getDouble("consommation"));
                                ressourcesArray.put(ressource);
                            }
                        }
                        batiment.put("ressources", ressourcesArray);
                        batimentsArray.put(batiment);
                    }
                }
                backup.put("batiments", batimentsArray);

                // Sauvegarde des étudiants
                JSONArray personnesArray = new JSONArray();

                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM personnes WHERE type='ETUDIANT'")) {
                    while (rs.next()) {
                        JSONObject ETUDIANT = new JSONObject();
                        ETUDIANT.put("id", rs.getInt("id"));
                        ETUDIANT.put("nom", rs.getString("nom"));
                        ETUDIANT.put("filiere", rs.getString("filiere"));
                        ETUDIANT.put("satisfaction", rs.getDouble("satisfaction"));
                        personnesArray.put(ETUDIANT);
                    }
                }
                backup.put("personnes", personnesArray);

                // Sauvegarde des professeurs
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM personnes WHERE type='PROFESSEUR'")) {
                    while (rs.next()) {
                        JSONObject PROFESSEUR = new JSONObject();
                        PROFESSEUR.put("id", rs.getInt("id"));
                        PROFESSEUR.put("nom", rs.getString("nom"));
                        PROFESSEUR.put("matiere", rs.getString("matiere"));
                        PROFESSEUR.put("satisfaction", rs.getDouble("satisfaction"));
                        PROFESSEUR.put("disponibilite", rs.getBoolean("disponibilite"));
                        personnesArray.put(PROFESSEUR);
                    }
                }
                backup.put("personnes", personnesArray);

                // Écrire dans le fichier
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(backup.toString(4)); // Indentation pour lisibilité
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Données sauvegardées avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
            }
        }
    }

    @FXML
    private void restoreData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restaurer les données");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
        File file = fileChooser.showOpenDialog(batimentsTable.getScene().getWindow());
        if (file != null) {
            try {
                // Lire le fichier JSON
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                }
                JSONObject backup = new JSONObject(content.toString());

                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Restaurer les bâtiments
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM batiment_ressources");
                        stmt.executeUpdate("DELETE FROM batiments");
                    }
                    JSONArray batimentsArray = backup.getJSONArray("batiments");
                    try (PreparedStatement batimentStmt = conn.prepareStatement(
                            "INSERT INTO batiments (id, nom, type, capacite, occupation_actuelle, impact_satisfaction) VALUES (?, ?, ?, ?, ?, ?)")) {
                        for (int i = 0; i < batimentsArray.length(); i++) {
                            JSONObject batiment = batimentsArray.getJSONObject(i);
                            batimentStmt.setInt(1, batiment.getInt("id"));
                            batimentStmt.setString(2, batiment.getString("nom"));
                            batimentStmt.setString(3, batiment.getString("type"));
                            batimentStmt.setInt(4, batiment.getInt("capacite"));
                            batimentStmt.setInt(5, batiment.getInt("occupation_actuelle"));
                            batimentStmt.setDouble(6, batiment.getDouble("impact_satisfaction"));
                            batimentStmt.executeUpdate();

                            // Restaurer les ressources
                            JSONArray ressourcesArray = batiment.getJSONArray("ressources");
                            try (PreparedStatement ressourceStmt = conn.prepareStatement(
                                    "INSERT INTO batiment_ressources (batiment_id, ressource, consommation) VALUES (?, ?, ?)")) {
                                for (int j = 0; j < ressourcesArray.length(); j++) {
                                    JSONObject ressource = ressourcesArray.getJSONObject(j);
                                    ressourceStmt.setInt(1, batiment.getInt("id"));
                                    ressourceStmt.setString(2, ressource.getString("ressource"));
                                    ressourceStmt.setDouble(3, ressource.getDouble("consommation"));
                                    ressourceStmt.executeUpdate();
                                }
                            }
                        }
                    }

                    // Restaurer les étudiants
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM personnes WHERE type='ETUDIANT'");
                    }
                    JSONArray etudiantsArray = backup.getJSONArray("personnes");
                    try (PreparedStatement etudiantStmt = conn.prepareStatement(
                            "INSERT INTO personnes (id, nom, filiere, satisfaction) VALUES (?, ?, ?, ?) WHERE type='ETUDIANT'")) {
                        for (int i = 0; i < etudiantsArray.length(); i++) {
                            JSONObject etudiant = etudiantsArray.getJSONObject(i);
                            etudiantStmt.setInt(1, etudiant.getInt("id"));
                            etudiantStmt.setString(2, etudiant.getString("nom"));
                            etudiantStmt.setString(3, etudiant.getString("filiere"));
                            etudiantStmt.setDouble(4, etudiant.getDouble("satisfaction"));
                            etudiantStmt.executeUpdate();
                        }
                    }

                    // Restaurer les professeurs
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM personnes WHERE type='PROFESSEUR'");
                    }
                    JSONArray professeursArray = backup.getJSONArray("personnes");
                    try (PreparedStatement profStmt = conn.prepareStatement(
                            "INSERT INTO personnes (id, nom, matiere, satisfaction, disponible) VALUES (?, ?, ?, ?, ?) WHERE type='PROFESSEUR'")) {
                        for (int i = 0; i < professeursArray.length(); i++) {
                            JSONObject professeur = professeursArray.getJSONObject(i);
                            profStmt.setInt(1, professeur.getInt("id"));
                            profStmt.setString(2, professeur.getString("nom"));
                            profStmt.setString(3, professeur.getString("matiere"));
                            profStmt.setDouble(4, professeur.getDouble("satisfaction"));
                            profStmt.setBoolean(5, professeur.getBoolean("disponible"));
                            profStmt.executeUpdate();
                        }
                    }

                    conn.commit();
                    loadBatiments();
                    loadStudents();
                    loadProfesseurs();
                    updateCharts();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Données restaurées avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la restauration : " + e.getMessage());
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la lecture du fichier : " + e.getMessage());
            }
        }
    }



}