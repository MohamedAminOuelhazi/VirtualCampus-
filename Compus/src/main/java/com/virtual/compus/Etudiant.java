package com.virtual.compus;

import javafx.beans.property.*;

public class Etudiant {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty nom = new SimpleStringProperty();
    private final SimpleStringProperty filiere = new SimpleStringProperty();
    private final DoubleProperty satisfaction = new SimpleDoubleProperty();
    private final SimpleIntegerProperty heuresCours = new SimpleIntegerProperty();

    public Etudiant(int id,String nom, String filiere, double satisfaction, int heuresCours) {
        this.id.set(id);
        this.nom.setValue(nom);
        this.filiere.setValue(filiere);
        setSatisfaction(satisfaction);
        this.heuresCours.setValue(heuresCours);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public SimpleStringProperty filiereProperty() {
        return filiere;
    }

    public DoubleProperty satisfactionProperty() {
        return satisfaction;
    }

    public SimpleIntegerProperty heuresCoursProperty() {
        return heuresCours;
    }

    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.getValue();
    }

    public String getFiliere() {
        return filiere.getValue();
    }

    public double getSatisfaction() {
        return satisfaction.get();
    }

    public int getHeuresCours() {
        return heuresCours.getValue();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setFiliere(String filiere) {
        this.filiere.set(filiere);
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction.set(Math.min(Math.max(satisfaction, 0.0), 100.0)); // Plafonner entre 0 et 100
    }

    public void setHeuresCours(int heuresCours) {
        this.heuresCours.set(heuresCours);
    }
}
