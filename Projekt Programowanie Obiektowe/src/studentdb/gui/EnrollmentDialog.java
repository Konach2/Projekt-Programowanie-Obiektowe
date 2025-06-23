package studentdb.gui;

import studentdb.models.*;
import studentdb.services.*;
import studentdb.exceptions.StudentManagementException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Dialog do zarządzania zapisami studentów na kursy.
 * Umożliwia zapisywanie i wypisywanie studentów z kursów.
 */
public class EnrollmentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private CourseService courseService;
    private StudentService studentService;
    private Course course;
    
    private JList<Student> availableStudentsList;
    private JList<Student> enrolledStudentsList;
    private DefaultListModel<Student> availableModel;
    private DefaultListModel<Student> enrolledModel;
    
    public EnrollmentDialog(JFrame parent, CourseService courseService, 
                           StudentService studentService, Course course) {
        super(parent, "Zarządzanie Zapisami - " + course.getName(), true);
        this.courseService = courseService;
        this.studentService = studentService;
        this.course = course;
        
        initializeGUI();
        loadStudents();
        
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Panel główny z dwiema listami
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Lista dostępnych studentów
        availableModel = new DefaultListModel<>();
        availableStudentsList = new JList<>(availableModel);
        availableStudentsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Dostępni Studenci"));
        availablePanel.add(new JScrollPane(availableStudentsList), BorderLayout.CENTER);
        
        // Lista zapisanych studentów
        enrolledModel = new DefaultListModel<>();
        enrolledStudentsList = new JList<>(enrolledModel);
        enrolledStudentsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.setBorder(BorderFactory.createTitledBorder(
            String.format("Zapisani Studenci (%d/%d)", 
            course.getEnrolledStudents().size(), course.getMaxStudents())));
        enrolledPanel.add(new JScrollPane(enrolledStudentsList), BorderLayout.CENTER);
        
        mainPanel.add(availablePanel);
        mainPanel.add(enrolledPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton enrollButton = new JButton("Zapisz →");
        enrollButton.addActionListener(e -> enrollSelectedStudents());
        buttonPanel.add(enrollButton);
        
        JButton unenrollButton = new JButton("← Wypisz");
        unenrollButton.addActionListener(e -> unenrollSelectedStudents());
        buttonPanel.add(unenrollButton);
        
        buttonPanel.add(Box.createHorizontalStrut(20));
        
        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel informacyjny
        JLabel infoLabel = new JLabel(String.format(
            "Kurs: %s | Wolne miejsca: %d", 
            course.getName(), course.getAvailableSpots()));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);
    }
    
    private void loadStudents() {
        availableModel.clear();
        enrolledModel.clear();
        
        List<Student> allStudents = studentService.getAllStudents();
        Set<String> enrolledIds = course.getEnrolledStudents();
        
        for (Student student : allStudents) {
            if (enrolledIds.contains(student.getStudentId())) {
                enrolledModel.addElement(student);
            } else {
                availableModel.addElement(student);
            }
        }
    }
    
    private void enrollSelectedStudents() {
        List<Student> selectedStudents = availableStudentsList.getSelectedValuesList();
        
        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz studentów do zapisania");
            return;
        }
        
        if (selectedStudents.size() > course.getAvailableSpots()) {
            JOptionPane.showMessageDialog(this, 
                String.format("Za dużo studentów! Dostępne miejsca: %d", 
                course.getAvailableSpots()));
            return;
        }
        
        int enrolled = 0;
        StringBuilder errors = new StringBuilder();
        
        for (Student student : selectedStudents) {
            try {
                if (courseService.enrollStudentInCourse(course.getCode(), student.getStudentId())) {
                    studentService.enrollStudentInCourse(student.getStudentId(), course.getCode());
                    enrolled++;
                }
            } catch (StudentManagementException e) {
                errors.append(student.getFullName()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        if (enrolled > 0) {
            // Odśwież dane kursu
            try {
                course = courseService.getCourse(course.getCode());
                loadStudents();
                JOptionPane.showMessageDialog(this, 
                    String.format("Zapisano %d studentów", enrolled));
            } catch (StudentManagementException e) {
                JOptionPane.showMessageDialog(this, "Błąd odświeżania: " + e.getMessage());
            }
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Błędy:\n" + errors.toString(), 
                                        "Błędy", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void unenrollSelectedStudents() {
        List<Student> selectedStudents = enrolledStudentsList.getSelectedValuesList();
        
        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz studentów do wypisania");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            String.format("Czy na pewno chcesz wypisać %d studentów?", selectedStudents.size()),
            "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        int unenrolled = 0;
        StringBuilder errors = new StringBuilder();
        
        for (Student student : selectedStudents) {
            try {
                if (courseService.unenrollStudentFromCourse(course.getCode(), student.getStudentId())) {
                    studentService.unenrollStudentFromCourse(student.getStudentId(), course.getCode());
                    unenrolled++;
                }
            } catch (StudentManagementException e) {
                errors.append(student.getFullName()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        if (unenrolled > 0) {
            // Odśwież dane kursu
            try {
                course = courseService.getCourse(course.getCode());
                loadStudents();
                JOptionPane.showMessageDialog(this, 
                    String.format("Wypisano %d studentów", unenrolled));
            } catch (StudentManagementException e) {
                JOptionPane.showMessageDialog(this, "Błąd odświeżania: " + e.getMessage());
            }
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Błędy:\n" + errors.toString(), 
                                        "Błędy", JOptionPane.WARNING_MESSAGE);
        }
    }
}
