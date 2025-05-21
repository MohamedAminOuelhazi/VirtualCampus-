package com.virtual.compus;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class loginController   {

    @FXML private TextField username;
    @FXML private PasswordField pwd;


    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ou ton mot de passe


    public static Connection getConnection() throws SQLException {
        System.out.println("test cnx 1");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public void loginButtonOnAction(ActionEvent actionEvent) {
        // Méthode pour le bouton Login
            String usern = username.getText();
            String password = pwd.getText();

            if (usern.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs");
                return;
            }

            try (Connection conn = getConnection()) {
                String sql = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, usern);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Login réussi
                    loadMainApplication();
                    closeCurrentWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec", "Identifiants incorrects");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Problème de connexion: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    // Méthode pour le bouton Cancel
    public void cancelButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    // Méthodes utilitaires
    private void loadMainApplication() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/virtual/compus/AdminMain.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

    }


    private void closeCurrentWindow() {
        ((Node) username).getScene().getWindow().hide();
    }



    private void showAlert(Alert.AlertType type, String title, String content) {

        System.out.println("test9");

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

