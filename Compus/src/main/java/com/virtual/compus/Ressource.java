package com.virtual.compus;

public enum Ressource {
    WIFI,
    ELECTRICITE,
    EAU,
    ESPACE;

    // Maximum allowed consumption for each resource (example values)
    public double getMaxConsommation() {
        switch (this) {
            case WIFI: return 1000.0; // e.g., Mbps
            case ELECTRICITE: return 5000.0; // e.g., kWh
            case EAU: return 10000.0; // e.g., liters
            case ESPACE: return 2000.0; // e.g., square meters
            default: return 0.0;
        }
    }
}