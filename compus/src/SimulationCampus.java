
import java.util.*;

class SimulationCampus {
    Campus campus;
    Random random;

    public SimulationCampus(Campus campus) {
        this.campus = campus;
        this.random = new Random();
    }

    public void journee() {
        System.out.println("--- Nouvelle journée de simulation ---");

        for (Personne p : campus.personnes) {
            if (p instanceof Etudiant) {
                ((Etudiant) p).assisterCours();
                ((Etudiant) p).exprimerSatisfaction();
            } else if (p instanceof Professeur) {
                ((Professeur) p).enseigner();
            }
        }

        campus.ressources.calculerConsommation();
        declencherEvenementAleatoire();
        campus.ressources.optimiserRessources();
        // campus.ressources.optimiserRessources(campus.getBatiments());

    }

    public void declencherEvenementAleatoire() {
        EvenementType[] evenements = EvenementType.values();
        EvenementType event = evenements[random.nextInt(evenements.length)];

        switch (event) {
            case GREVE_PROF:
                System.out.println("Événement: Grève des professeurs ! Satisfaction des étudiants diminue.");
                for (Personne p : campus.personnes) {
                    if (p instanceof Professeur) {
                        ((Professeur) p).changerDisponibilite(false);
                        ((Professeur) p).satisfaction -= 20;
                    }
                }
                for (Personne p : campus.personnes) {
                    if (p instanceof Etudiant) {
                        ((Etudiant) p).satisfaction -= 10;
                    }
                }
                break;
            case COUPURE_WIFI:
                System.out.println("Événement: Coupure Wi-Fi ! Les cours sont perturbés.");
                campus.ressources.wifi = 0;
                break;
            case INFESTATION_CAFETERIA:
                System.out.println("Événement: Cafétéria infestée ! Fermeture temporaire.");
                for (Personne p : campus.personnes) {
                    p.satisfaction -= 10;
                }
                break;
            case EXAMENS:
                System.out.println("Événement: Examens en approche ! Stress accru.");
                for (Personne p : campus.personnes) {
                    if (p instanceof Etudiant) {
                        ((Etudiant) p).satisfaction -= 5;
                    }
                }

                break;
            case JOURNEE_PORTES_OUVERTES:
                System.out.println("Événement: Journée portes ouvertes ! Afflux de visiteurs.");
                // effet fictif sur espace
                campus.ressources.espace -= 50;
                System.out.println("Événement: début du Wi-Fi diminue.");
                campus.ressources.wifi -= 50;
                break;
        }
    }
}

enum EvenementType {
    GREVE_PROF, COUPURE_WIFI, INFESTATION_CAFETERIA, EXAMENS, JOURNEE_PORTES_OUVERTES
}
