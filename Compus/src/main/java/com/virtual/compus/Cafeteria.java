package com.virtual.compus;

public class Cafeteria extends Batiment {
    public Cafeteria(int id, String nom, String type, int capacite, double impactSatisfaction,int occupationActuelle) {
        super(id, nom, "Cafétéria", capacite, impactSatisfaction,0);
        setConsommationRessource(Ressource.ELECTRICITE, 120.0);
        setConsommationRessource(Ressource.EAU, 200.0);
        setConsommationRessource(Ressource.ESPACE, 50.0);
    }

    @Override
    public void utiliserRessources() {
        System.out.println("Cafétéria " + getNom() + " utilise " + getConsommationRessources().get(Ressource.ELECTRICITE) + " kWh, " + getConsommationRessources().get(Ressource.EAU) + " litres d'eau, et " + getConsommationRessources().get(Ressource.ESPACE) + " m² d'espace.");
    }
}