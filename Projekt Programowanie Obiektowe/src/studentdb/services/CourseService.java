package studentdb.services;

import studentdb.models.Course;
import studentdb.exceptions.*;
import studentdb.utils.FileManager;

import java.util.*;

/**
 * Serwis zarządzania kursami.
 * Demonstruje zastosowanie zasad SOLID.
 */
public class CourseService {
    private Map<String, Course> courses;
    private FileManager fileManager;
    
    public CourseService(FileManager fileManager) {
        this.fileManager = fileManager;
        this.courses = new HashMap<>();
        loadCourses();
    }
    
    public void addCourse(String code, String name, String description, int credits, 
                         int maxStudents) throws StudentManagementException {
        
        if (courses.containsKey(code)) {
            return; // Po prostu pomijamy jeśli już istnieje
        }
        
        Course course = new Course(code, name, description, credits, maxStudents);
        courses.put(code, course);
        saveCourses();
    }
    
    public Course getCourse(String code) throws CourseNotFoundException {
        Course course = courses.get(code);
        if (course == null) {
            throw new CourseNotFoundException(code);
        }
        return course;
    }
    
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }
    
    public List<Course> getAvailableCourses() {
        List<Course> available = new ArrayList<>();
        for (Course course : courses.values()) {
            if (course.hasAvailableSpots()) {
                available.add(course);
            }
        }
        return available;
    }
    
    public boolean enrollStudentInCourse(String courseCode, String studentId) 
            throws StudentManagementException {
        Course course = getCourse(courseCode);
        
        if (course.getEnrolledStudents().contains(studentId)) {
            return false; // Już zapisany
        }
        
        if (!course.hasAvailableSpots()) {
            throw new StudentManagementException("Brak wolnych miejsc na kursie: " + course.getName());
        }
        
        boolean enrolled = course.enrollStudent(studentId);
        if (enrolled) {
            saveCourses();
        }
        return enrolled;
    }
    
    public boolean unenrollStudentFromCourse(String courseCode, String studentId) 
            throws StudentManagementException {
        Course course = getCourse(courseCode);
        
        if (!course.getEnrolledStudents().contains(studentId)) {
            throw new StudentManagementException("Student nie jest zapisany na ten kurs");
        }
        
        boolean unenrolled = course.unenrollStudent(studentId);
        if (unenrolled) {
            saveCourses();
        }
        return unenrolled;
    }
    
    public void updateCourse(Course course) throws StudentManagementException {
        if (!courses.containsKey(course.getCode())) {
            throw new CourseNotFoundException(course.getCode());
        }
        courses.put(course.getCode(), course);
        saveCourses();
    }
    
    public void removeCourse(String code) throws CourseNotFoundException {
        if (!courses.containsKey(code)) {
            throw new CourseNotFoundException(code);
        }
        courses.remove(code);
        saveCourses();
    }
    
    private void loadCourses() {
        try {
            Map<String, Course> loadedCourses = fileManager.loadCourses();
            if (loadedCourses != null) {
                this.courses = loadedCourses;
            }
        } catch (Exception e) {
            System.err.println("Błąd ładowania kursów: " + e.getMessage());
        }
    }
    
    private void saveCourses() {
        try {
            fileManager.saveCourses(courses);
        } catch (Exception e) {
            System.err.println("Błąd zapisywania kursów: " + e.getMessage());
        }
    }
}
