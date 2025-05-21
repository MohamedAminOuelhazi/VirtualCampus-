package com.virtual.compus;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

public class SimulationCampus {
    private ObservableList<Batiment> batimentsList;
    private ObservableList<Etudiant> etudiantsList;
    private ObservableList<Professeur> professeursList;
    private Random random;
    private String lastEventMessage;

    // Constructor
    public SimulationCampus(ObservableList<Batiment> batimentsList, ObservableList<Etudiant> etudiantsList, ObservableList<Professeur> professeursList) {
        this.batimentsList = batimentsList;
        this.etudiantsList = etudiantsList;
        this.professeursList = professeursList;
        this.random = new Random();
        this.lastEventMessage = "Aucun événement";
    }

    // Simulate a single cycle (e.g., one day)
    public void simulerCycle() {

//        Reset occupancy for normal day simulation
//        for (Batiment batiment : batimentsList) {
//            // Normal day: occupancy is between 50% and 90% of capacity
//            int normalOccupancy = batiment.getCapacite() * (50 + random.nextInt(41)) / 100;
//            batiment.setOccupationActuelle(normalOccupancy);
//        }

//        if (random.nextDouble() < 0.5) {
    //
//        } else {
//            lastEventMessage = "Aucun événement";
//        }

        triggerRandomEvent();

        updateMetrics();
    }

    // Trigger a random event
    private void triggerRandomEvent() {
        int eventType = random.nextInt(5); // 0 to 4 for 5 events
        switch (eventType) {
            case 0:
                journeePortesOuvertes();
                lastEventMessage = "Journée portes ouvertes - Pic de visiteurs !";
                break;
            case 1:
                greveDesProfs();
                lastEventMessage = "Grève des profs - Perte de satisfaction !";
                break;
            case 2:
                coupureWifi();
                lastEventMessage = "Coupure Wi-Fi - Cours bloqués !";
                break;
            case 3:
                cafeteriaInfestee();
                lastEventMessage = "Cafétéria infestée - Fermeture temporaire !";
                break;
            case 4:
                examensApproche();
                lastEventMessage = "Examens en approche - Stress élevé !";
                break;
        }
        System.out.println("Événement: " + lastEventMessage);
    }

    private void journeePortesOuvertes() {
        for (Batiment batiment : batimentsList) {
            int extraOccupancy = batiment.getCapacite() * (120 + random.nextInt(31)) / 100;
            batiment.setOccupationActuelle(extraOccupancy);
            double currentElectricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
            double currentWifi = batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
            batiment.setConsommationRessource(Ressource.ELECTRICITE, currentElectricite * 1.2); // Augmentation de 20%
            batiment.setConsommationRessource(Ressource.WIFI, currentWifi * 1.5); // Augmentation de 50%
        }
    }

    // Event: Grève des profs
    private void greveDesProfs() {
        for (Professeur prof : professeursList) {
            prof.setSatisfaction(prof.getSatisfaction() - 10); // Decrease satisfaction
            prof.setDisponible(false); // Professors on strike
        }
        for (Etudiant etudiant : etudiantsList) {
            etudiant.setSatisfaction(etudiant.getSatisfaction() - 5); // Students lose satisfaction
        }
        // Réduire les ressources de 10% (par exemple, ELECTRICITE et WIFI)
        for (Batiment batiment : batimentsList) {
            double currentElectricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
            double currentWifi = batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
            batiment.setConsommationRessource(Ressource.ELECTRICITE, currentElectricite * 0.9); // Réduction de 10%
            batiment.setConsommationRessource(Ressource.WIFI, currentWifi * 0.9); // Réduction de 10%
        }
        System.out.println("Événement: Grève des profs - Perte de satisfaction !");
    }


    // Event: Coupure Wi-Fi
    private void coupureWifi() {
        for (Batiment batiment : batimentsList) {
            batiment.setConsommationRessource(Ressource.WIFI, 0.0); // Wi-Fi is down
            if (batiment.getType().equals("Salle de Cours")) {
                batiment.setOccupationActuelle(0); // Classes canceled
            }
            // Réduire ELECTRICITE de 10% à cause de la coupure
            double currentElectricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
            batiment.setConsommationRessource(Ressource.ELECTRICITE, currentElectricite * 0.9);
        }

        System.out.println("Événement: Coupure Wi-Fi - Cours bloqués !");
    }

    // Event: Cafétéria infestée
    private void cafeteriaInfestee() {
        for (Batiment batiment : batimentsList) {
            if (batiment.getType().equals("Cafétéria")) {
                batiment.setOccupationActuelle(0); // Cafeteria closed
                batiment.setImpactSatisfaction(batiment.getImpactSatisfaction() - 2); // Negative impact
            }
            // Réduire EAU et ELECTRICITE de 10% (fermeture de la cafétéria)
            double currentEau = batiment.getConsommationRessources().getOrDefault(Ressource.EAU, 0.0);
            double currentElectricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
            batiment.setConsommationRessource(Ressource.EAU, currentEau * 0.9);
            batiment.setConsommationRessource(Ressource.ELECTRICITE, currentElectricite * 0.9);
        }
        for (Etudiant etudiant : etudiantsList) {
            etudiant.setSatisfaction(etudiant.getSatisfaction() - 40); // Students unhappy
        }
        for (Professeur professeur : professeursList) {
            professeur.setSatisfaction(professeur.getSatisfaction() - 40); // Students unhappy
        }



        System.out.println("Événement: Cafétéria infestée - Fermeture temporaire !");
    }

    // Event: Examens en approche
    private void examensApproche() {
        for (Etudiant etudiant : etudiantsList) {
            etudiant.setSatisfaction(etudiant.getSatisfaction() - 10); // Increased stress
        }
        for (Batiment batiment : batimentsList) {
            if (batiment.getType().equals("Bibliothèque")) {
                batiment.setOccupationActuelle(batiment.getCapacite() * 110 / 100);
                // Augmentation de l'ELECTRICITE et du WIFI due à l'affluence
                double currentElectricite = batiment.getConsommationRessources().getOrDefault(Ressource.ELECTRICITE, 0.0);
                double currentWifi = batiment.getConsommationRessources().getOrDefault(Ressource.WIFI, 0.0);
                batiment.setConsommationRessource(Ressource.ELECTRICITE, currentElectricite * 1.1); // Augmentation de 10%
                batiment.setConsommationRessource(Ressource.WIFI, currentWifi * 1.2); // Augmentation de 20%
            }
        }
        System.out.println("Événement: Examens en approche - Stress élevé !");
    }

    // Update metrics after simulation
    private void updateMetrics() {
        for (Batiment batiment : batimentsList) {
            // If occupancy exceeds capacity, reduce satisfaction
            if (batiment.getOccupationActuelle() > batiment.getCapacite()) {
                double satisfactionReduction = (batiment.getOccupationActuelle() - batiment.getCapacite()) * 0.1;
                batiment.setImpactSatisfaction(batiment.getImpactSatisfaction() - satisfactionReduction);
            }
        }
    }

    // Method to save changes to the database
    public void saveToDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Update batiments
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE batiments SET occupation_actuelle = ?, impact_satisfaction = ? WHERE id = ?")) {
                for (Batiment batiment : batimentsList) {
                    stmt.setInt(1, batiment.getOccupationActuelle());
                    stmt.setDouble(2, batiment.getImpactSatisfaction());
                    stmt.setInt(3, batiment.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // Update batiment_ressources
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM batiment_ressources WHERE batiment_id = ?");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO batiment_ressources (batiment_id, ressource, consommation) VALUES (?, ?, ?)")) {
                for (Batiment batiment : batimentsList) {
                    // Delete existing resources
                    deleteStmt.setInt(1, batiment.getId());
                    deleteStmt.addBatch();

                    // Insert updated resources
                    for (Map.Entry<Ressource, Double> entry : batiment.getConsommationRessources().entrySet()) {
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

            // Update etudiants and professeurs satisfaction
            try (PreparedStatement etudiantStmt = conn.prepareStatement("UPDATE personnes SET satisfaction = ? WHERE id = ? AND type = 'ETUDIANT'");
                 PreparedStatement profStmt = conn.prepareStatement("UPDATE personnes SET satisfaction = ?, disponibilite = ? WHERE id = ? AND type='PROFESSOR'")) {
                for (Etudiant etudiant : etudiantsList) {
                    etudiantStmt.setDouble(1, etudiant.getSatisfaction());
                    etudiantStmt.setInt(2, etudiant.getId());
                    etudiantStmt.addBatch();
                }
                for (Professeur prof : professeursList) {
                    profStmt.setDouble(1, prof.getSatisfaction());
                    profStmt.setBoolean(2, prof.getDisponible());
                    profStmt.setInt(3, prof.getId());
                    profStmt.addBatch();
                }
                etudiantStmt.executeBatch();
                profStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error (e.g., rollback, show alert)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLastEventMessage() {
        return lastEventMessage;
    }
}