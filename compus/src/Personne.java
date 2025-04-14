
abstract class Personne {
    protected String nom;
    double satisfaction;

    public Personne(String nom) {
        this.nom = nom;
        this.satisfaction = 100.0;
    }

    public abstract void exprimerSatisfaction();

    public abstract void consommerRessource(String ressource);

}

class Etudiant extends Personne {
    private String filiere;
    private int heuresCours;

    public Etudiant(String nom, String filiere, int heuresCours) {
        super(nom);
        this.filiere = filiere;
        this.heuresCours = heuresCours;
    }

    public void assisterCours() {
        heuresCours++;
        satisfaction += 10; // perte légère de satisfaction
        System.out.println(nom + " a assisté à un cours. Satisfaction: " + satisfaction);
    }

    @Override
    public void consommerRessource(String ressource) {
        System.out.println(nom + " a consommé la ressource: " + ressource);
        satisfaction += 5; // gain de satisfaction
        if (satisfaction > 100) {
            satisfaction = 100; // Limite de satisfaction
        }
    }

    @Override
    public void exprimerSatisfaction() {
        System.out.println("Satisfaction de " + nom + " : " + satisfaction + "%");
    }
}

class Professeur extends Personne {
    private String matiere;
    private boolean disponible;

    public Professeur(String nom, String matiere, boolean disponible) {
        super(nom);
        this.matiere = matiere;
        this.disponible = disponible;
    }

    public void enseigner() {
        if (disponible) {
            System.out.println(nom + " enseigne la matière: " + matiere);
        } else {
            System.out.println(nom + " n'est pas disponible pour enseigner.");
        }
    }

    @Override
    public void consommerRessource(String ressource) {
        System.out.println(nom + " a consommé la ressource: " + ressource);
        satisfaction += 5; // gain de satisfaction
        if (satisfaction > 100) {
            satisfaction = 100; // Limite de satisfaction
        }
    }

    @Override
    public void exprimerSatisfaction() {
        System.out.println(nom + " est satisfait de l'enseignement : " + satisfaction + "%");
    }

    public void changerDisponibilite(boolean dispo) {
        this.disponible = dispo;
        System.out.println(nom + " est maintenant " + (disponible ? "disponible" : "indisponible") + ".");
    }
}
