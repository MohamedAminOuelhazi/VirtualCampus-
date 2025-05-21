package com.virtual.compus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierEtudiantController {

    @FXML private TextField nomField;
    @FXML private TextField filiereField;
    @FXML private TextField heuresCoursField;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private AdminController parentController;
    private Etudiant etudiant;

    public void setParentController(AdminController parentController) {
        this.parentController = parentController;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
        // Pré-remplir les champs avec les données de l'étudiant
        nomField.setText(etudiant.getNom());
        filiereField.setText(etudiant.getFiliere());
        heuresCoursField.setText(String.valueOf(etudiant.getHeuresCours()));
    }

    @FXML
    private void saveModifications(ActionEvent event) {
        String nom = nomField.getText().trim();
        String filiere = filiereField.getText().trim();
        String heuresCoursText = heuresCoursField.getText().trim();

        // Validation des champs
        if (nom.isEmpty() || filiere.isEmpty() || heuresCoursText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        double satisfaction;
        int heuresCours;
        try {
            heuresCours = Integer.parseInt(heuresCoursText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "La satisfaction et les heures de cours doivent être des nombres valides.");
            return;
        }

        if (heuresCours < 0) {
            showAlert(Alert.AlertType.ERROR, "Valeur invalide", "Les heures de cours doivent être positives.");
            return;
        }

        // Mettre à jour l'étudiant dans la base de données
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE personnes SET nom = ?, filiere = ?, heures_cours = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, filiere);
            pstmt.setInt(3, heuresCours);
            pstmt.setInt(4, etudiant.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Recharger les étudiants via AdminController
                if (parentController != null) {
                    parentController.loadStudents();
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Étudiant modifié avec succès.");
                // Fermer la fenêtre après sauvegarde
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun étudiant trouvé avec cet ID.");
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