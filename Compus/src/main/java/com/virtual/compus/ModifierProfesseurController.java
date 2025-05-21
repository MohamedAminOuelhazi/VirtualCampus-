package com.virtual.compus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProfesseurController {

    @FXML private TextField nomField;
    @FXML private TextField matiereField;
    @FXML private CheckBox dispoCheckBox;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private AdminController parentController;
    private Professeur professeur;

    public void setParentController(AdminController parentController) {
        this.parentController = parentController;
    }

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;

        // Pré-remplir les champs avec les données du professeur
        nomField.setText(professeur.getNom());
        matiereField.setText(professeur.getMatiere());
        dispoCheckBox.setSelected(professeur.getDisponible());
    }

    @FXML
    private void saveModifications(ActionEvent event) {
        String nom = nomField.getText().trim();
        String matiere = matiereField.getText().trim();
        boolean disponibilite = dispoCheckBox.isSelected();

        // Validation des champs
        if (nom.isEmpty() || matiere.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        // Mettre à jour le professeur dans la base de données
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE personnes SET nom = ?, matiere = ?, disponibilite = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, matiere);
            pstmt.setBoolean(3, disponibilite);
            pstmt.setInt(4, professeur.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Recharger les professeurs via AdminMainController
                if (parentController != null) {
                    parentController.loadProfesseurs();
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Professeur modifié avec succès.");
                // Fermer la fenêtre après sauvegarde
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun professeur trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}