package com.virtual.compus;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddBatimentController implements Initializable {
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField capaciteField;
    @FXML private TextField impactSatisfactionField;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the ComboBox with building types
        typeCombo.setItems(FXCollections.observableArrayList(
                "Salle de Cours",
                "Bibliothèque",
                "Cafétéria",
                "Laboratoire"
        ));
    }
    // Variables pour stocker les ressources disponibles
    private double availableWifi;
    private double availableElectricite;
    private double availableEau;
    private double availableEspace;

    // Variables pour stocker les maximums (pour référence)
    private double wifiMax;
    private double electriciteMax;
    private double eauMax;
    private double espaceMax;

    // Méthode pour recevoir les ressources disponibles depuis AdminController
    public void setAvailableResources(double availableWifi, double availableElectricite, double availableEau, double availableEspace) {
        this.availableWifi = availableWifi;
        this.availableElectricite = availableElectricite;
        this.availableEau = availableEau;
        this.availableEspace = availableEspace;

        // Stocker les maximums pour référence
        this.wifiMax = Ressource.WIFI.getMaxConsommation();
        this.electriciteMax = Ressource.ELECTRICITE.getMaxConsommation();
        this.eauMax = Ressource.EAU.getMaxConsommation();
        this.espaceMax = Ressource.ESPACE.getMaxConsommation();
    }

    @FXML
    private void addBatiment() {
        String nom = nomField.getText();
        String type = typeCombo.getValue();
        String capaciteText = capaciteField.getText();
        String impactSatisfactionText = impactSatisfactionField.getText();

        if (nom.isEmpty() || type == null || capaciteText.isEmpty() || impactSatisfactionText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        int capacite;
        double impactSatisfaction;
        try {
            capacite = Integer.parseInt(capaciteText);
            impactSatisfaction = Double.parseDouble(impactSatisfactionText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Capacité et Impact Satisfaction doivent être des nombres.");
            return;
        }

        // Définir les consommations en fonction du type
        double electricite = 0, eau = 0, espace = 0, wifi = 0;
        if (type.equals("Salle de Cours")) {
            electricite = 1200.0;
            eau = 2500.0;
            espace = 500.0;
            wifi = 300.0;
        } else if (type.equals("Bibliothèque")) {
            electricite = 2000.0;
            eau = 1800.0;
            espace = 1000.0;
            wifi = 400.0;
        } else if (type.equals("Laboratoire")) {
            electricite = 1500.0;
            eau = 900.0;
            espace = 400.0;
            wifi = 500.0;
        } else if (type.equals("Cafétéria")) {
            electricite = 800.0;
            eau = 4000.0;
            espace = 600.0;
            wifi = 150.0;
        }

        // Vérifier les ressources disponibles (comparer avec les ressources restantes, pas les maximums)
        if (wifi > availableWifi || electricite > availableElectricite || eau > availableEau || espace > availableEspace) {
            showAlert("Erreur", "Ressources disponibles insuffisantes pour ajouter ce bâtiment.\n" +
                    "WiFi disponible: " + availableWifi + "\n" +
                    "Électricité disponible: " + availableElectricite + "\n" +
                    "Eau disponible: " + availableEau + "\n" +
                    "Espace disponible: " + availableEspace);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into batiments table
            int batimentId;
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO batiments (nom, type, capacite, impact_satisfaction) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nom);
                stmt.setString(2, type);
                stmt.setInt(3, capacite);
                stmt.setDouble(4, impactSatisfaction);
                stmt.executeUpdate();

                // Get the generated batiment ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    batimentId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve batiment ID.");
                }
            }

            // Insert default resource consumptions (example values, adjust as needed)
            try (PreparedStatement ressourceStmt = conn.prepareStatement("INSERT INTO batiment_ressources (batiment_id, ressource, consommation) VALUES (?, ?, ?)")) {

                if (type.equals("Salle de Cours")) {
                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ELECTRICITE");
                    ressourceStmt.setDouble(3, 1200.0); // Default value, adjust based on type
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ESPACE");
                    ressourceStmt.setDouble(3, 500.0); // Example: ESPACE based on capacity
                    ressourceStmt.executeUpdate();


                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "EAU");
                    ressourceStmt.setDouble(3, 2500.0);
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "WIFI");
                    ressourceStmt.setDouble(3, 300.0);
                    ressourceStmt.executeUpdate();
                }
                if (type.equals("Bibliothèque")) {
                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ELECTRICITE");
                    ressourceStmt.setDouble(3, 2000.0); // Default value, adjust based on type
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ESPACE");
                    ressourceStmt.setDouble(3, 1000.0); // Example: ESPACE based on capacity
                    ressourceStmt.executeUpdate();


                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "EAU");
                    ressourceStmt.setDouble(3, 1800.0);
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "WIFI");
                    ressourceStmt.setDouble(3, 400.0);
                    ressourceStmt.executeUpdate();
                }

                if (type.equals("Laboratoire")) {
                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ELECTRICITE");
                    ressourceStmt.setDouble(3, 1500.0); // Default value, adjust based on type
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ESPACE");
                    ressourceStmt.setDouble(3, 400.0); // Example: ESPACE based on capacity
                    ressourceStmt.executeUpdate();


                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "EAU");
                    ressourceStmt.setDouble(3, 900.0);
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "WIFI");
                    ressourceStmt.setDouble(3, 500.0);
                    ressourceStmt.executeUpdate();
                }

                if (type.equals("Cafétéria")) {
                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ELECTRICITE");
                    ressourceStmt.setDouble(3, 800.0); // Default value, adjust based on type
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "ESPACE");
                    ressourceStmt.setDouble(3, 600.0); // Example: ESPACE based on capacity
                    ressourceStmt.executeUpdate();


                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "EAU");
                    ressourceStmt.setDouble(3, 4000.0);
                    ressourceStmt.executeUpdate();

                    ressourceStmt.setInt(1, batimentId);
                    ressourceStmt.setString(2, "WIFI");
                    ressourceStmt.setDouble(3, 150.0);
                    ressourceStmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            showAlert("Succès", "Bâtiment ajouté avec succès.");
            closeWindow();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            showAlert("Erreur", "Erreur lors de l'ajout du bâtiment: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}