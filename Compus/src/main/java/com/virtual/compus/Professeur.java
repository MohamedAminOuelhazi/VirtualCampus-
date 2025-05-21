package com.virtual.compus;

import javafx.beans.property.*;

public class Professeur {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty matiere = new SimpleStringProperty();
    private final BooleanProperty disponible = new SimpleBooleanProperty();
    private final DoubleProperty satisfaction = new SimpleDoubleProperty();



    public Professeur(int id, String nom, String matiere, boolean disponible, int satisfaction) {
        this.id.set(id);
        this.nom.set(nom);
        this.matiere.set(matiere);
        this.disponible.set(disponible);
        setSatisfaction(satisfaction);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty matiereProperty() {
        return matiere;
    }

    public BooleanProperty disponibleProperty() {
        return disponible;
    }
    public DoubleProperty satisfactionProperty() {
        return satisfaction;
    }

    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getMatiere() {
        return matiere.get();
    }

    public boolean getDisponible() {
        return disponible.get();
    }

    public double getSatisfaction() {
        return satisfaction.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setMatiere(String matiere) {
        this.matiere.set(matiere);
    }

    public void setDisponible(boolean disponible) {
        this.disponible.set(disponible);
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction.set(Math.min(Math.max(satisfaction, 0.0), 100.0)); // Plafonner entre 0 et 100
    }
}