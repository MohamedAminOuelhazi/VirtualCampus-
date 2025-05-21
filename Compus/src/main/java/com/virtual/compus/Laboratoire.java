package com.virtual.compus;

public class Laboratoire extends Batiment {
    public Laboratoire(int id, String nom, String type, int capacite, double impactSatisfaction,int occupationActuelle) {
        super(id, nom, "Laboratoire", capacite, impactSatisfaction,0);
        setConsommationRessource(Ressource.ELECTRICITE, 150.0);
        setConsommationRessource(Ressource.EAU, 300.0);
    }

    @Override
    public void utiliserRessources() {
        System.out.println("Laboratoire " + getNom() + " utilise " + getConsommationRessources().get(Ressource.ELECTRICITE) + " kWh et " + getConsommationRessources().get(Ressource.EAU) + " litres d'eau.");
    }
}