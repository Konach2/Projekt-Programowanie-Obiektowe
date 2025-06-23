package studentdb.models;

import java.io.Serializable;
import java.util.*;

/**
 * Klasa reprezentująca kurs akademicki.
 * Demonstruje enkapsulację i użycie kolekcji.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private int credits;
    private int maxStudents;
    private Set<String> enrolledStudents; // Użycie kolekcji Set
    
    public Course(String code, String name, String description, int credits, int maxStudents) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.maxStudents = maxStudents;
        this.enrolledStudents = new HashSet<>();
    }
    
    public boolean canEnrollStudent() {
        return enrolledStudents.size() < maxStudents;
    }
    
    public boolean hasAvailableSpots() {
        return canEnrollStudent();
    }
    
    public boolean enrollStudent(String studentId) {
        if (canEnrollStudent() && !enrolledStudents.contains(studentId)) {
            enrolledStudents.add(studentId);
            return true;
        }
        return false;
    }
    
    public boolean unenrollStudent(String studentId) {
        return enrolledStudents.remove(studentId);
    }
    
    public int getAvailableSpots() {
        return maxStudents - enrolledStudents.size();
    }
    
    // Gettery i settery
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    
    public Set<String> getEnrolledStudents() { return new HashSet<>(enrolledStudents); }
    
    public String toString() {
        return code + " - " + name + " (" + enrolledStudents.size() + "/" + maxStudents + ")";
    }
    
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(code, course.code);
    }
    
    public int hashCode() {
        return Objects.hash(code);
    }
}
