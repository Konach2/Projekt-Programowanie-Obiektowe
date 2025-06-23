package studentdb.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstrakcyjna klasa bazowa reprezentująca osobę.
 * Demonstruje użycie abstrakcji i dziedziczenia.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String firstName;
    protected String lastName;
    protected String email;
    protected LocalDate birthDate;
    
    public Person(String firstName, String lastName, String email, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
    }
    
    // Metoda abstrakcyjna - musi być zaimplementowana w klasach potomnych
    public abstract String getRole();
    
    // Metoda wirtualna - może być przesłonięta w klasach potomnych
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Gettery i settery
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    // Dodana metoda do obliczania wieku
    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    public String toString() {
        return getFullName() + " (" + getRole() + ")";
    }
    
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(email, person.email);
    }
    
    public int hashCode() {
        return Objects.hash(email);
    }
}
