package com.virtual.compus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class AddProfesseurController {

    @FXML private TextField nomField;
    @FXML private TextField matiereField;
    @FXML private CheckBox dispoCheckBox;

    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private AdminController parentController;

    // Méthode pour définir le contrôleur parent
    public void setParentController(AdminController parentController) {
        this.parentController = parentController;
    }


    public void saveProfesseur(ActionEvent actionEvent) {

        String nom = nomField.getText().trim();
        String matiere = matiereField.getText().trim();
        boolean disponibilite = dispoCheckBox.isSelected();

        if (nom.isEmpty() || matiere.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs");
            return;
        }

        try (Connection conn = java.sql.DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO personnes (nom, matiere, disponibilite,type) VALUES (?, ?, ?,'PROFESSEUR')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, matiere);
            pstmt.setBoolean(3, disponibilite);

            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Professeur ajouté avec succès.");

            parentController.loadProfesseurs();

            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
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
        matiereField.clear();
        dispoCheckBox.setSelected(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
