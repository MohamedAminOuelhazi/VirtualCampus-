package com.virtual.compus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddEtudiantController {


    @FXML private TextField nomField;
    @FXML private TextField filiereField;
    @FXML private TextField heuresField;

    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private AdminController parentController;

    // Méthode pour définir le contrôleur parent
    public void setParentController(AdminController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void saveEtudiant(ActionEvent event) {
        String nom = nomField.getText();
        String filiere = filiereField.getText();
        String heures = heuresField.getText();

        if (nom.isEmpty() || filiere.isEmpty() || heures.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs");
            return;
        }

        try (Connection conn = java.sql.DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO personnes (nom, filiere, heures_cours,type) VALUES (?, ?, ?,'ETUDIANT')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, filiere);
            pstmt.setInt(3, Integer.parseInt(heures));

            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Étudiant ajouté avec succès");

            parentController.loadStudents();

            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les heures doivent être un nombre valide");
        }
    }




    @FXML
    private void goBack(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        nomField.clear();
        filiereField.clear();
        heuresField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
