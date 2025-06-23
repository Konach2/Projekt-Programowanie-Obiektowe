package studentdb.utils;

import studentdb.models.*;
import studentdb.services.*;
import studentdb.exceptions.StudentManagementException;

import java.time.LocalDate;

/**
 * Prosta klasa do ładowania przykładowych danych testowych.
 * Czyści stare dane i ładuje nowe, spójne dane.
 */
public class SampleDataLoader {
    
    public static void loadSampleData(StudentService studentService, CourseService courseService) {
        clearAllData(studentService, courseService);
        loadSampleDataForce(studentService, courseService);
    }
    
    public static void loadSampleDataForce(StudentService studentService, CourseService courseService) {
        try {
            System.out.println("=== CZYSZCZENIE I ŁADOWANIE NOWYCH DANYCH ===");
            
            // Wyczyść wszystkie stare dane
            clearAllData(studentService, courseService);
            
            // 1. Najpierw kursy
            loadCleanCourses(courseService);
            
            // 2. Potem studenci (tylko 6 z prostymi ID)
            loadCleanStudents(studentService);
            
            // 3. Zapisy na kursy (każdy student na inne kursy)
            enrollStudentsClean(studentService, courseService);
            
            // 4. Oceny
            loadCleanGrades(studentService);
            
            System.out.println("=== NOWE DANE ZAŁADOWANE ===");
            
        } catch (Exception e) {
            System.err.println("Błąd ładowania danych: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void clearAllData(StudentService studentService, CourseService courseService) {
        System.out.println("Czyszczenie starych danych...");
        
        try {
            // Usuń wszystkich studentów
            for (Student student : studentService.getAllStudents()) {
                studentService.removeStudent(student.getStudentId());
            }
            
            // Usuń wszystkie kursy
            for (Course course : courseService.getAllCourses()) {
                courseService.removeCourse(course.getCode());
            }
            
            System.out.println("Stare dane wyczyszczone");
        } catch (Exception e) {
            System.out.println("Błąd czyszczenia danych: " + e.getMessage());
        }
    }
    
    private static void loadCleanCourses(CourseService courseService) {
        System.out.println("Ładowanie 5 kursów...");
        
        try {
            courseService.addCourse("PO001", "Programowanie Obiektowe", "Podstawy OOP w Java", 6, 25);
            courseService.addCourse("BD001", "Bazy Danych", "SQL i projektowanie BD", 5, 30);
            courseService.addCourse("ALG001", "Algorytmy", "Struktury danych i algorytmy", 7, 20);
            courseService.addCourse("MAT001", "Matematyka", "Matematyka dyskretna", 4, 35);
            courseService.addCourse("WEB001", "Programowanie Web", "HTML, CSS, JavaScript", 5, 25);
            
            System.out.println("✓ Dodano 5 kursów");
        } catch (Exception e) {
            System.err.println("Błąd dodawania kursów: " + e.getMessage());
        }
    }
    
    private static void loadCleanStudents(StudentService studentService) {
        System.out.println("Ładowanie 6 studentów...");
        
        try {
            studentService.addStudent("S001", "Jan", "Kowalski", "jan@student.pl",
                LocalDate.of(2003, 5, 15), "Informatyka", 2, Student.StudentType.FULL_TIME);
            
            studentService.addStudent("S002", "Anna", "Nowak", "anna@student.pl",
                LocalDate.of(2003, 8, 22), "Informatyka", 2, Student.StudentType.FULL_TIME);
            
            studentService.addStudent("S003", "Piotr", "Wiśniewski", "piotr@student.pl",
                LocalDate.of(2004, 3, 10), "Informatyka", 1, Student.StudentType.FULL_TIME);
            
            studentService.addStudent("S004", "Maria", "Wójcik", "maria@student.pl",
                LocalDate.of(2002, 12, 5), "Informatyka", 3, Student.StudentType.FULL_TIME);
            
            studentService.addStudent("S005", "Tomasz", "Zieliński", "tomasz@student.pl",
                LocalDate.of(2003, 6, 18), "Informatyka", 2, Student.StudentType.PART_TIME);
            
            studentService.addStudent("S006", "Katarzyna", "Lewandowska", "kasia@student.pl",
                LocalDate.of(2004, 1, 25), "Informatyka", 1, Student.StudentType.FULL_TIME);
            
            System.out.println("✓ Dodano 6 studentów (S001-S006)");
        } catch (Exception e) {
            System.err.println("Błąd dodawania studentów: " + e.getMessage());
        }
    }
    
    private static void enrollStudentsClean(StudentService studentService, CourseService courseService) {
        System.out.println("Zapisywanie studentów na różne kursy...");
        
        // S001 - PO, BD, MAT (3 kursy)
        enrollStudent(studentService, courseService, "S001", "PO001");
        enrollStudent(studentService, courseService, "S001", "BD001");
        enrollStudent(studentService, courseService, "S001", "MAT001");
        
        // S002 - PO, ALG, WEB (3 kursy)
        enrollStudent(studentService, courseService, "S002", "PO001");
        enrollStudent(studentService, courseService, "S002", "ALG001");
        enrollStudent(studentService, courseService, "S002", "WEB001");
        
        // S003 - PO, MAT (2 kursy)
        enrollStudent(studentService, courseService, "S003", "PO001");
        enrollStudent(studentService, courseService, "S003", "MAT001");
        
        // S004 - BD, ALG, WEB (3 kursy)
        enrollStudent(studentService, courseService, "S004", "BD001");
        enrollStudent(studentService, courseService, "S004", "ALG001");
        enrollStudent(studentService, courseService, "S004", "WEB001");
        
        // S005 - PO, BD (2 kursy)
        enrollStudent(studentService, courseService, "S005", "PO001");
        enrollStudent(studentService, courseService, "S005", "BD001");
        
        // S006 - MAT, WEB (2 kursy)
        enrollStudent(studentService, courseService, "S006", "MAT001");
        enrollStudent(studentService, courseService, "S006", "WEB001");
        
        System.out.println("✓ Zakończono zapisy - każdy student na inne kursy");
    }
    
    private static void enrollStudent(StudentService studentService, CourseService courseService, 
                                    String studentId, String courseCode) {
        try {
            studentService.enrollStudentInCourse(studentId, courseCode);
            courseService.enrollStudentInCourse(courseCode, studentId);
            System.out.println("  ✓ " + studentId + " → " + courseCode);
        } catch (Exception e) {
            System.out.println("  ✗ Błąd: " + studentId + " → " + courseCode + " (" + e.getMessage() + ")");
        }
    }
    
    private static void loadCleanGrades(StudentService studentService) {
        System.out.println("Dodawanie ocen...");
        
        // S001: PO, BD, MAT
        addGrade(studentService, "S001", "PO001", 4.5, "Projekt OOP", Grade.GradeType.PROJECT);
        addGrade(studentService, "S001", "BD001", 5.0, "Egzamin SQL", Grade.GradeType.EXAM);
        addGrade(studentService, "S001", "MAT001", 4.0, "Kolokwium", Grade.GradeType.TEST);
        
        // S002: PO, ALG, WEB
        addGrade(studentService, "S002", "PO001", 3.5, "Projekt", Grade.GradeType.PROJECT);
        addGrade(studentService, "S002", "ALG001", 4.5, "Implementacja", Grade.GradeType.HOMEWORK);
        addGrade(studentService, "S002", "WEB001", 5.0, "Strona WWW", Grade.GradeType.PROJECT);
        
        // S003: PO, MAT
        addGrade(studentService, "S003", "PO001", 3.0, "Kolokwium", Grade.GradeType.TEST);
        addGrade(studentService, "S003", "MAT001", 4.0, "Zadania", Grade.GradeType.HOMEWORK);
        
        // S004: BD, ALG, WEB
        addGrade(studentService, "S004", "BD001", 4.5, "Projekt bazy", Grade.GradeType.PROJECT);
        addGrade(studentService, "S004", "ALG001", 4.0, "Egzamin", Grade.GradeType.EXAM);
        addGrade(studentService, "S004", "WEB001", 3.5, "Witryna", Grade.GradeType.PROJECT);
        
        // S005: PO, BD
        addGrade(studentService, "S005", "PO001", 3.5, "Zaliczenie", Grade.GradeType.TEST);
        addGrade(studentService, "S005", "BD001", 4.0, "Projekt", Grade.GradeType.PROJECT);
        
        // S006: MAT, WEB
        addGrade(studentService, "S006", "MAT001", 5.0, "Egzamin", Grade.GradeType.EXAM);
        addGrade(studentService, "S006", "WEB001", 4.5, "Aplikacja", Grade.GradeType.PROJECT);
        
        System.out.println("✓ Dodano oceny dla wszystkich zapisanych kursów");
    }
    
    private static void addGrade(StudentService studentService, String studentId, String courseCode, 
                               double value, String description, Grade.GradeType type) {
        try {
            Grade grade = new Grade(value, description, LocalDate.now().minusDays((long)(Math.random() * 30)), type);
            studentService.addGradeToStudent(studentId, courseCode, grade);
            System.out.println("  ✓ " + studentId + " - " + courseCode + ": " + value);
        } catch (Exception e) {
            System.out.println("  ✗ Błąd oceny " + studentId + " - " + courseCode + ": " + e.getMessage());
        }
    }
}
