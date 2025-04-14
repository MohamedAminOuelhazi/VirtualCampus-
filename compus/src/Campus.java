
import java.util.*;

class Campus {
    List<Batiment> batiments;
    List<Personne> personnes;
    Ressource ressources;

    public Campus(Ressource ressources) {
        this.batiments = new ArrayList<>();
        this.personnes = new ArrayList<>();
        this.ressources = ressources;
    }

    public void ajouterBatiment(Batiment b) {
        batiments.add(b);
        System.out.println("Bâtiment ajouté : " + b.nom);
    }

    public void assignerPersonne(Personne p) {
        personnes.add(p);
        System.out.println("Personne assignée : " + p.nom);
    }

    public Map<String, Object> genererStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("Nombre de bâtiments", batiments.size());
        stats.put("Nombre de personnes", personnes.size());
        stats.put("Consommation totale ressources", ressources.calculerConsommation());

        double satisfactionMoyenne = personnes.stream()
                .filter(p -> p instanceof Etudiant)
                .mapToDouble(p -> ((Etudiant) p).satisfaction)
                .average().orElse(0);

        stats.put("Satisfaction moyenne des étudiants", satisfactionMoyenne);

        System.out.println("Statistiques générées : " + stats);
        return stats;
    }
}
