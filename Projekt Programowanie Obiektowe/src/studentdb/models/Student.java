package studentdb.models;

import java.time.LocalDate;
import java.util.*;

/**
 * Klasa reprezentująca studenta - dziedziczy po Person.
 * Demonstruje dziedziczenie i enkapsulację.
 */
public class Student extends Person {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String fieldOfStudy;
    private int semester;
    private StudentType type;
    private Set<String> enrolledCourses; // Użycie kolekcji Set
    private Map<String, List<Grade>> grades; // Użycie kolekcji Map i List
    
    public enum StudentType {
        FULL_TIME("Stacjonarne"),
        PART_TIME("Niestacjonarne"),
        EVENING("Wieczorowe");
        
        private final String displayName;
        
        StudentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Student(String studentId, String firstName, String lastName, String email, 
                  LocalDate birthDate, String fieldOfStudy, int semester, StudentType type) {
        super(firstName, lastName, email, birthDate);
        this.studentId = studentId;
        this.fieldOfStudy = fieldOfStudy;
        this.semester = semester;
        this.type = type;
        this.enrolledCourses = new HashSet<>();
        this.grades = new HashMap<>();
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    @Override
    public String getFullName() {
        return super.getFullName() + " (" + studentId + ")";
    }
    
    public void enrollInCourse(String courseCode) {
        enrolledCourses.add(courseCode);
        grades.putIfAbsent(courseCode, new ArrayList<>());
    }
    
    public void unenrollFromCourse(String courseCode) {
        enrolledCourses.remove(courseCode);
        grades.remove(courseCode);
    }
    
    public boolean isEnrolledInCourse(String courseCode) {
        return enrolledCourses.contains(courseCode);
    }
    
    public void addGrade(String courseCode, Grade grade) {
        if (enrolledCourses.contains(courseCode)) {
            grades.get(courseCode).add(grade);
        }
    }
    
    public double calculateGPA() {
        if (grades.isEmpty()) return 0.0;
        
        double totalPoints = 0.0;
        int totalGrades = 0;
        
        for (List<Grade> courseGrades : grades.values()) {
            for (Grade grade : courseGrades) {
                totalPoints += grade.getValue();
                totalGrades++;
            }
        }
        
        return totalGrades > 0 ? totalPoints / totalGrades : 0.0;
    }
    
    public List<String> getEnrolledCoursesSorted() {
        List<String> sortedCourses = new ArrayList<>(enrolledCourses);
        Collections.sort(sortedCourses);
        return sortedCourses;
    }
    
    // Gettery i settery
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public StudentType getType() { return type; }
    public void setType(StudentType type) { this.type = type; }
    
    public Set<String> getEnrolledCourses() { return new HashSet<>(enrolledCourses); }
    public Map<String, List<Grade>> getGrades() { return new HashMap<>(grades); }
}
