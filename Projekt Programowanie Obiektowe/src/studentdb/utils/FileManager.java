package studentdb.utils;

import studentdb.models.*;
import java.io.*;
import java.util.Map;

/**
 * Klasa zarządzająca zapisem i odczytem danych z plików.
 * Demonstruje użycie serializacji do utrwalania danych.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.dat";
    private static final String COURSES_FILE = DATA_DIR + File.separator + "courses.dat";
    
    public FileManager() {
        createDataDirectory();
    }
    
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Student> loadStudents() throws IOException, ClassNotFoundException {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Student>) ois.readObject();
        }
    }
    
    public void saveStudents(Map<String, Student> students) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STUDENTS_FILE))) {
            oos.writeObject(students);
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Course> loadCourses() throws IOException, ClassNotFoundException {
        File file = new File(COURSES_FILE);
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Course>) ois.readObject();
        }
    }
    
    public void saveCourses(Map<String, Course> courses) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COURSES_FILE))) {
            oos.writeObject(courses);
        }
    }
}
