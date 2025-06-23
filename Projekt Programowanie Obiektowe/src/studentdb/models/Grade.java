package studentdb.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Klasa reprezentująca ocenę studenta z kursu.
 * Demonstruje enkapsulację i walidację danych.
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double value;
    private String description;
    private LocalDate date;
    private GradeType type;
    
    public enum GradeType {
        EXAM("Egzamin"),
        TEST("Kolokwium"),
        PROJECT("Projekt"),
        HOMEWORK("Zadanie domowe"),
        PARTICIPATION("Aktywność");
        
        private final String displayName;
        
        GradeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Grade(double value, String description, LocalDate date, GradeType type) {
        if (value < 2.0 || value > 5.0) {
            throw new IllegalArgumentException("Ocena musi być w zakresie 2.0 - 5.0");
        }
        this.value = value;
        this.description = description;
        this.date = date;
        this.type = type;
    }
    
    public String getGradeLetter() {
        if (value >= 4.5) return "A";
        if (value >= 3.5) return "B";
        if (value >= 3.0) return "C";
        if (value >= 2.5) return "D";
        return "F";
    }
    
    // Gettery i settery
    public double getValue() { return value; }
    public void setValue(double value) {
        if (value < 2.0 || value > 5.0) {
            throw new IllegalArgumentException("Ocena musi być w zakresie 2.0 - 5.0");
        }
        this.value = value;
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public GradeType getType() { return type; }
    public void setType(GradeType type) { this.type = type; }
    
    @Override
    public String toString() {
        return String.format("%.1f (%s) - %s [%s]", value, getGradeLetter(), description, type.getDisplayName());
    }
}
