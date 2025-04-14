
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class HelloFX extends Application {
    Campus campus;
    SimulationCampus simulation;

    Label labelStats;
    ProgressBar wifiBar, electricBar, waterBar;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation (dummy)
        Ressource ressources = new Ressource(100, 100, 100, 100);
        campus = new Campus(ressources);
        simulation = new SimulationCampus(campus);

        // UI layout
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        Button btnSimulerJournee = new Button("Simuler une journée");
        btnSimulerJournee.setOnAction(e -> simulerJournee());

        labelStats = new Label("Statistiques :");

        // Progress Bars for resources
        wifiBar = new ProgressBar(1);
        electricBar = new ProgressBar(1);
        waterBar = new ProgressBar(1);

        VBox resourceBars = new VBox(5,
                new Label("Wi-Fi"), wifiBar,
                new Label("Électricité"), electricBar,
                new Label("Eau"), waterBar);

        // Chart
        PieChart satisfactionChart = new PieChart();
        satisfactionChart.setTitle("Satisfaction Étudiants");

        // Add to layout
        root.getChildren().addAll(btnSimulerJournee, labelStats, resourceBars, satisfactionChart);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation Campus Intelligent");
        primaryStage.show();

    }

    private void simulerJournee() {
        simulation.journee();
        Map<String, Object> stats = campus.genererStatistiques();

        double wifi = campus.ressources.wifi / 100.0;
        double elec = campus.ressources.electricite / 100.0;
        double eau = campus.ressources.eau / 100.0;

        wifiBar.setProgress(wifi);
        electricBar.setProgress(elec);
        waterBar.setProgress(eau);

        labelStats.setText("Statistiques :\n" +
                "Bâtiments: " + stats.get("Nombre de bâtiments") + "\n" +
                "Personnes: " + stats.get("Nombre de personnes") + "\n" +
                "Consommation: " + stats.get("Consommation totale ressources") + "\n" +
                "Satisfaction: " + stats.get("Satisfaction moyenne des étudiants"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}