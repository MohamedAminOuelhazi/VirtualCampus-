package com.virtual.compus;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/compus";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ou ton mot de passe

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Charger explicitement le driver MySQL
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("✅ Connexion réussie !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver non trouvé : " + e.getMessage());
        }

}

}
