package com.bibliotheque.util;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    // Calcule les jours de retard
    public static int calculerJoursRetard(LocalDate dateRetourPrevue, LocalDate dateRetourEffective) {
        if (dateRetourEffective == null || dateRetourPrevue == null) {
            return 0;
        }
        // Si retourné à temps ou en avance
        if (dateRetourEffective.isBefore(dateRetourPrevue) || dateRetourEffective.isEqual(dateRetourPrevue)) {
            return 0;
        }
        // Calcule la différence en jours
        return (int) ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
    }
    
    // Calcule la pénalité avec un taux personnalisé
    public static double calculerPenalite(int joursRetard, double tauxParJour) {
        if (joursRetard <= 0) {
            return 0.0;
        }
        return joursRetard * tauxParJour;
    }
    
    // Calcule la pénalité avec taux par défaut (0.50d/jour)
    public static double calculerPenalite(int joursRetard) {
        return calculerPenalite(joursRetard, 0.50);
    }
    
    // Formate une date pour l'affichage
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "Non spécifié";
        }
        return date.toString();
    }
    
    // Génère une date de retour prévue (2 semaines après aujourd'hui)
    public static LocalDate genererDateRetourPrevue() {
        return LocalDate.now().plusWeeks(2);
    }
}
