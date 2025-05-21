package com.virtual.compus;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifyBatimentController {
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField capaciteField;
    @FXML private TextField impactSatisfactionField;
    @FXML private TextField occupationActuelleField;

    private Batiment batiment;

    public void setBatiment(Batiment batiment) {
        this.batiment = batiment;
        nomField.setText(batiment.getNom());
        typeCombo.setValue(batiment.getType());
        capaciteField.setText(String.valueOf(batiment.getCapacite()));
        impactSatisfactionField.setText(String.valueOf(batiment.getImpactSatisfaction()));
        occupationActuelleField.setText(String.valueOf(batiment.getOccupationActuelle()));
    }

    @FXML
    private void modifyBatiment() {
        String nom = nomField.getText();
        String type = typeCombo.getValue();
        String capaciteText = capaciteField.getText();
        String impactSatisfactionText = impactSatisfactionField.getText();
        String occupationActuelleText = occupationActuelleField.getText();

        if (nom.isEmpty() || type == null || capaciteText.isEmpty() || impactSatisfactionText.isEmpty()|| occupationActuelleText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        int capacite;
        int occupationActuelle;
        double impactSatisfaction;
        try {
            capacite = Integer.parseInt(capaciteText);
            impactSatisfaction = Double.parseDouble(impactSatisfactionText);
            occupationActuelle = Integer.parseInt(occupationActuelleText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Capacité et Impact Satisfaction doivent être des nombres.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Update the batiments table
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE batiments SET nom = ?, type = ?, capacite = ?, impact_satisfaction = ? ,occupation_actuelle = ?WHERE id = ?")) {
                stmt.setString(1, nom);
                stmt.setString(2, type);
                stmt.setInt(3, capacite);
                stmt.setDouble(4, impactSatisfaction);
                stmt.setInt(5, occupationActuelle);
                stmt.setInt(5, batiment.getId());
                stmt.executeUpdate();
            }

            // Update resource consumptions (delete and re-insert for simplicity)
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM batiment_ressources WHERE batiment_id = ?")) {
                deleteStmt.setInt(1, batiment.getId());
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement ressourceStmt = conn.prepareStatement("INSERT INTO batiment_ressources (batiment_id, ressource, consommation) VALUES (?, ?, ?)")) {
                // Re-insert resource consumptions (example values, adjust as needed)
                ressourceStmt.setInt(1, batiment.getId());
                ressourceStmt.setString(2, "ELECTRICITE");
                ressourceStmt.setDouble(3, 50.0); // Adjust based on actual data
                ressourceStmt.executeUpdate();

                ressourceStmt.setInt(1, batiment.getId());
                ressourceStmt.setString(2, "ESPACE");
                ressourceStmt.setDouble(3, capacite);
                ressourceStmt.executeUpdate();

                if (type.equals("Cafétéria") || type.equals("Laboratoire")) {
                    ressourceStmt.setInt(1, batiment.getId());
                    ressourceStmt.setString(2, "EAU");
                    ressourceStmt.setDouble(3, 50.0);
                    ressourceStmt.executeUpdate();
                }
            }

            conn.commit();
            showAlert("Succès", "Bâtiment modifié avec succès.");
            closeWindow();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            showAlert("Erreur", "Erreur lors de la modification du bâtiment: " + e.getMessage());
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