package com.virtual.compus;

public class Bibliotheque extends Batiment {
    public Bibliotheque(int id, String nom, String type, int capacite, double impactSatisfaction,int occupationActuelle) {
        super(id, nom, "Bibliothèque", capacite, impactSatisfaction,0);
        setConsommationRessource(Ressource.ELECTRICITE, 80.0);
        setConsommationRessource(Ressource.ESPACE, 100.0);
    }

    @Override
    public void utiliserRessources() {
        System.out.println("Bibliothèque " + getNom() + " utilise " + getConsommationRessources().get(Ressource.ELECTRICITE) + " kWh et " + getConsommationRessources().get(Ressource.ESPACE) + " m² d'espace.");
    }
}