package studentdb.services;

import studentdb.models.*;
import studentdb.exceptions.*;
import studentdb.utils.FileManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serwis zarządzania studentami.
 * Demonstruje zastosowanie zasad SOLID i użycie kolekcji.
 */
public class StudentService {
    private Map<String, Student> students; // Użycie kolekcji Map
    private FileManager fileManager;
    
    public StudentService(FileManager fileManager) {
        this.fileManager = fileManager;
        this.students = new HashMap<>();
        loadStudents();
    }
    
    public void addStudent(String studentId, String firstName, String lastName, String email,
                          LocalDate birthDate, String fieldOfStudy, int semester, 
                          Student.StudentType type) throws StudentManagementException {
        
        if (students.containsKey(studentId)) {
            return; // Po prostu pomijamy jeśli już istnieje
        }
        
        Student student = new Student(studentId, firstName, lastName, email, birthDate,
                                    fieldOfStudy, semester, type);
        students.put(studentId, student);
        saveStudents();
    }
    
    public Student getStudent(String studentId) throws StudentNotFoundException {
        Student student = students.get(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }
    
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>(students.values());
        studentList.sort(Comparator.comparing(Student::getStudentId));
        return studentList;
    }
    
    public List<Student> getAllStudentsSortedByName() {
        List<Student> studentList = new ArrayList<>(students.values());
        studentList.sort(Comparator.comparing(Student::getLastName)
                                  .thenComparing(Student::getFirstName));
        return studentList;
    }
    
    public List<Student> searchStudents(String query) {
        List<Student> results = students.values().stream()
                .filter(student -> 
                    student.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    student.getStudentId().toLowerCase().contains(query.toLowerCase()) ||
                    student.getFieldOfStudy().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(Student::getStudentId))
                .collect(Collectors.toList());
        return results;
    }
    
    public void enrollStudentInCourse(String studentId, String courseCode) 
            throws StudentManagementException {
        Student student = getStudent(studentId);
        
        if (student.isEnrolledInCourse(courseCode)) {
            return; // Po prostu pomijamy jeśli już zapisany
        }
        
        student.enrollInCourse(courseCode);
        saveStudents();
    }
    
    public void unenrollStudentFromCourse(String studentId, String courseCode) 
            throws StudentManagementException {
        Student student = getStudent(studentId);
        
        if (!student.isEnrolledInCourse(courseCode)) {
            throw new StudentManagementException("Student nie jest zapisany na ten kurs");
        }
        
        student.unenrollFromCourse(courseCode);
        saveStudents();
    }
    
    public List<Student> getStudentsEnrolledInCourse(String courseCode) {
        return students.values().stream()
                .filter(student -> student.isEnrolledInCourse(courseCode))
                .sorted(Comparator.comparing(Student::getStudentId))
                .collect(Collectors.toList());
    }
    
    // Zmieniono nazwę metody z getStudentSubjects na getStudentCourses
    public List<String> getStudentCourses(String studentId) throws StudentNotFoundException {
        Student student = getStudent(studentId);
        return student.getEnrolledCoursesSorted();
    }
    
    public void updateStudent(Student student) throws StudentManagementException {
        if (!students.containsKey(student.getStudentId())) {
            throw new StudentNotFoundException(student.getStudentId());
        }
        students.put(student.getStudentId(), student);
        saveStudents();
    }
    
    public void removeStudent(String studentId) throws StudentNotFoundException {
        if (!students.containsKey(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        students.remove(studentId);
        saveStudents();
    }
    
    public void addGradeToStudent(String studentId, String courseCode, Grade grade) 
            throws StudentManagementException {
        Student student = getStudent(studentId);
        
        if (!student.isEnrolledInCourse(courseCode)) {
            throw new StudentManagementException("Student nie jest zapisany na ten kurs");
        }
        
        student.addGrade(courseCode, grade);
        saveStudents();
    }
    
    private void loadStudents() {
        try {
            Map<String, Student> loadedStudents = fileManager.loadStudents();
            if (loadedStudents != null) {
                this.students = loadedStudents;
            }
        } catch (Exception e) {
            System.err.println("Błąd ładowania studentów: " + e.getMessage());
        }
    }
    
    private void saveStudents() {
        try {
            fileManager.saveStudents(students);
        } catch (Exception e) {
            System.err.println("Błąd zapisywania studentów: " + e.getMessage());
        }
    }
}
