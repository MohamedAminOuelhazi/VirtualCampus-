import java.util.List;

class Ressource {
    double wifi, electricite, eau, espace;

    public Ressource(double wifi, double electricite, double eau, double espace) {
        this.wifi = wifi;
        this.electricite = electricite;
        this.eau = eau;
        this.espace = espace;
    }

    public double calculerConsommation() {
        double total = wifi + electricite + eau + espace;
        System.out.println("Consommation totale: " + total);
        return total;
    }

    // List<Batiment> batiments
    public void optimiserRessources() {
        if (wifi < 20) {
            System.out.println("Réduction de l'utilisation du Wi-Fi dans les zones non essentielles.");
            wifi += 10;
        }
        if (electricite < 20) {
            System.out.println("Extinction automatique des lumières dans les bâtiments vides.");
            electricite += 10;
        }
        if (eau < 20) {
            System.out.println(" Réduction de l'accès à l'eau dans certaines zones.");
            eau += 10;
        }
        /*
         * for (Batiment b : batiments) {
         * if (b.getCapacite() > 0 && b.getOccupation() > b.getCapacite()) {
         * System.out.println(
         * "[Optimisation] Trop d'occupation dans " + b.getNom() +
         * ". Recherche de salle alternative...");
         * // Idée : on chercherait une salle avec de la place et on y déplacerait des
         * // étudiants
         * }
         * }
         */

    }
}
