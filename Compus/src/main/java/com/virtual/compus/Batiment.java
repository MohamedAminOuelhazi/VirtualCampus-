package com.virtual.compus;

import java.util.HashMap;
import java.util.Map;

public abstract class Batiment {
    private int id;
    private String nom;
    private String type;
    private int capacite;
    private int occupationActuelle;
    private Map<Ressource, Double> consommationRessources;
    private double impactSatisfaction;

    public Batiment(int id, String nom, String type, int capacite, double impactSatisfaction,int occupationActuelle) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.occupationActuelle = occupationActuelle;
        this.consommationRessources = new HashMap<>();
        this.impactSatisfaction = impactSatisfaction;
    }

    public Map<Ressource, Double> getConsommationRessources() {
        return consommationRessources;
    }

    public void setConsommationRessource(Ressource ressource, double consommation) {
        double maxConsommation = ressource.getMaxConsommation();
        // Plafonner la consommation entre 0 et le maximum de la ressource
        consommation = Math.min(Math.max(consommation, 0.0), maxConsommation);
        consommationRessources.put(ressource, consommation);
    }

    public double calculerConsommation() {
        return consommationRessources.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getOccupationActuelle() {
        return occupationActuelle;
    }

    public void setOccupationActuelle(int occupationActuelle) {
        this.occupationActuelle = occupationActuelle;
    }

    public double getImpactSatisfaction() {
        return impactSatisfaction;
    }

    public void setImpactSatisfaction(double impactSatisfaction) {
        this.impactSatisfaction = impactSatisfaction;
    }



    // Optimize resources (reduce consumption if possible)
    public void optimiserRessources() {
        consommationRessources.replaceAll((ressource, consommation) -> {
            double max = ressource.getMaxConsommation();
            double optimized = Math.min(consommation, max * 0.8); // Reduce to 80% of max if exceeded
            return optimized;
        });
    }

    public abstract void utiliserRessources();
}