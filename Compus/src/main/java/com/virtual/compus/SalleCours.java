package com.virtual.compus;

public class SalleCours extends Batiment {
    public SalleCours(int id, String nom, String type, int capacite, double impactSatisfaction,int occupationActuelle) {
        super(id, nom, "Salle de Cours", capacite, impactSatisfaction,0);
        setConsommationRessource(Ressource.ELECTRICITE, 50.0);
        setConsommationRessource(Ressource.ESPACE, 30.0);
    }

    @Override
    public void utiliserRessources() {
        System.out.println("Salle de Cours " + getNom() + " utilise " + getConsommationRessources().get(Ressource.ELECTRICITE) + " kWh d'électricité.");
    }
}