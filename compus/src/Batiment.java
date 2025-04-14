
import java.util.Map;

abstract class Batiment {
    protected int id;
    protected String nom;
    protected String type;
    protected int capacite;
    protected Map<String, Double> consommationRessources;
    protected double impactSatisfaction;

    public Batiment(int id, String nom, String type, int capacite, Map<String, Double> consommationRessources,
            double impactSatisfaction) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.consommationRessources = consommationRessources;
        this.impactSatisfaction = impactSatisfaction;
    }

    public abstract void afficherDetails();
}

class SalleCours extends Batiment {
    public SalleCours(int id, String nom, int capacite, Map<String, Double> consommationRessources,
            double impactSatisfaction) {
        super(id, nom, "Salle de cours", capacite, consommationRessources, impactSatisfaction);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Salle de cours: " + nom + ", Capacité: " + capacite);
    }
}

class Bibliotheque extends Batiment {
    public Bibliotheque(int id, String nom, int capacite, Map<String, Double> consommationRessources,
            double impactSatisfaction) {
        super(id, nom, "Bibliothèque", capacite, consommationRessources, impactSatisfaction);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Bibliothèque: " + nom + ", Capacité: " + capacite);
    }
}

class Cafeteria extends Batiment {
    public Cafeteria(int id, String nom, int capacite, Map<String, Double> consommationRessources,
            double impactSatisfaction) {
        super(id, nom, "Cafétéria", capacite, consommationRessources, impactSatisfaction);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Cafétéria: " + nom + ", Capacité: " + capacite);
    }
}

class Laboratoire extends Batiment {
    public Laboratoire(int id, String nom, int capacite, Map<String, Double> consommationRessources,
            double impactSatisfaction) {
        super(id, nom, "Laboratoire", capacite, consommationRessources, impactSatisfaction);
    }

    @Override
    public void afficherDetails() {
        System.out.println("Laboratoire: " + nom + ", Capacité: " + capacite);
    }
}
