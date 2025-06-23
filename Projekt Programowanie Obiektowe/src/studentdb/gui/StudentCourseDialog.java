package studentdb.gui;

import studentdb.models.*;
import studentdb.services.*;
import studentdb.exceptions.StudentManagementException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog do zarządzania kursami studenta.
 * Umożliwia zapisywanie i wypisywanie z kursów.
 */
public class StudentCourseDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private StudentService studentService;
    private CourseService courseService;
    private Student student;
    
    private JList<Course> availableCoursesList;
    private JList<Course> enrolledCoursesList;
    private DefaultListModel<Course> availableModel;
    private DefaultListModel<Course> enrolledModel;
    
    public StudentCourseDialog(JFrame parent, StudentService studentService, 
                              CourseService courseService, Student student) {
        super(parent, "Zarządzanie Kursami - " + student.getFullName(), true);
        this.studentService = studentService;
        this.courseService = courseService;
        this.student = student;
        
        initializeGUI();
        loadCourses();
        
        setSize(700, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Panel główny z dwiema listami
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Lista dostępnych kursów
        availableModel = new DefaultListModel<>();
        availableCoursesList = new JList<>(availableModel);
        availableCoursesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Dostępne Kursy"));
        availablePanel.add(new JScrollPane(availableCoursesList), BorderLayout.CENTER);
        
        // Lista kursów studenta
        enrolledModel = new DefaultListModel<>();
        enrolledCoursesList = new JList<>(enrolledModel);
        enrolledCoursesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.setBorder(BorderFactory.createTitledBorder("Kursy Studenta"));
        enrolledPanel.add(new JScrollPane(enrolledCoursesList), BorderLayout.CENTER);
        
        mainPanel.add(availablePanel);
        mainPanel.add(enrolledPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton enrollButton = new JButton("Zapisz na kurs →");
        enrollButton.addActionListener(e -> enrollInSelectedCourses());
        buttonPanel.add(enrollButton);
        
        JButton unenrollButton = new JButton("← Wypisz z kursu");
        unenrollButton.addActionListener(e -> unenrollFromSelectedCourses());
        buttonPanel.add(unenrollButton);
        
        buttonPanel.add(Box.createHorizontalStrut(20));
        
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> loadCourses());
        buttonPanel.add(refreshButton);
        
        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel informacyjny
        JLabel infoLabel = new JLabel(String.format(
            "Student: %s | Kierunek: %s | Semestr: %d", 
            student.getFullName(), student.getFieldOfStudy(), student.getSemester()));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);
    }
    
    private void loadCourses() {
        availableModel.clear();
        enrolledModel.clear();
        
        try {
            List<Course> allCourses = courseService.getAllCourses();
            List<String> enrolledCourseCodes = studentService.getStudentCourses(student.getStudentId());
            
            for (Course course : allCourses) {
                if (enrolledCourseCodes.contains(course.getCode())) {
                    enrolledModel.addElement(course);
                } else if (course.hasAvailableSpots()) {
                    availableModel.addElement(course);
                }
            }
        } catch (StudentManagementException e) {
            JOptionPane.showMessageDialog(this, "Błąd ładowania kursów: " + e.getMessage());
        }
    }
    
    private void enrollInSelectedCourses() {
        List<Course> selectedCourses = availableCoursesList.getSelectedValuesList();
        
        if (selectedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz kursy do zapisania");
            return;
        }
        
        int enrolled = 0;
        StringBuilder errors = new StringBuilder();
        
        for (Course course : selectedCourses) {
            try {
                studentService.enrollStudentInCourse(student.getStudentId(), course.getCode());
                courseService.enrollStudentInCourse(course.getCode(), student.getStudentId());
                enrolled++;
            } catch (StudentManagementException e) {
                errors.append(course.getName()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        if (enrolled > 0) {
            loadCourses();
            JOptionPane.showMessageDialog(this, 
                String.format("Zapisano na %d kursów", enrolled));
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Błędy:\n" + errors.toString(), 
                                        "Błędy", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void unenrollFromSelectedCourses() {
        List<Course> selectedCourses = enrolledCoursesList.getSelectedValuesList();
        
        if (selectedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz kursy do wypisania");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            String.format("Czy na pewno chcesz wypisać studenta z %d kursów?\n" +
                         "Zostaną usunięte wszystkie oceny z tych kursów!", selectedCourses.size()),
            "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        int unenrolled = 0;
        StringBuilder errors = new StringBuilder();
        
        for (Course course : selectedCourses) {
            try {
                studentService.unenrollStudentFromCourse(student.getStudentId(), course.getCode());
                courseService.unenrollStudentFromCourse(course.getCode(), student.getStudentId());
                unenrolled++;
            } catch (StudentManagementException e) {
                errors.append(course.getName()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        if (unenrolled > 0) {
            loadCourses();
            JOptionPane.showMessageDialog(this, 
                String.format("Wypisano z %d kursów", unenrolled));
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Błędy:\n" + errors.toString(), 
                                        "Błędy", JOptionPane.WARNING_MESSAGE);
        }
    }
}
