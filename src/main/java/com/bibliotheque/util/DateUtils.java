package com.bibliotheque.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static long calculateDaysLate(LocalDate retourPrevue, LocalDate retourReelle) {
        if (retourReelle == null) return 0;
        return ChronoUnit.DAYS.between(retourPrevue, retourReelle);
    }
    
    public static double calculatePenalty(long daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * 0.50; // 0.50€ par jour de retard
    }
}// Utilitaires dates 
