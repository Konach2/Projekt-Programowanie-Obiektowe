package studentdb.gui;

import studentdb.models.*;
import studentdb.services.*;
import studentdb.exceptions.StudentManagementException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel zarządzania ocenami.
 * Demonstruje użycie zaawansowanych komponentów Swing.
 */
public class GradePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private StudentService studentService;
    private CourseService courseService;
    private JComboBox<Student> studentComboBox;
    private JComboBox<Course> courseComboBox;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    
    // Pola formularza ocen
    private JSpinner gradeSpinner;
    private JTextField descriptionField;
    private JComboBox<Grade.GradeType> gradeTypeComboBox;
    
    public GradePanel(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
        initializeGUI();
        
        // Dodaj opóźnienie i wymuś odświeżenie
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadData();
                System.out.println("GradePanel: Załadowano dane");
            }
        });
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Panel wyboru studenta i kursu
        add(createSelectionPanel(), BorderLayout.NORTH);
        
        // Tabela ocen
        add(createGradeTablePanel(), BorderLayout.CENTER);
        
        // Panel dodawania ocen
        add(createGradeFormPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Wybór Studenta i Kursu"));
        
        panel.add(new JLabel("Student:"));
        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(250, 25));
        // Ustaw maksymalną liczbę widocznych elementów w dropdown
        studentComboBox.setMaximumRowCount(10);
        studentComboBox.addActionListener(e -> {
            loadCourses();
            loadGrades();
        });
        panel.add(studentComboBox);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("Kurs:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 25));
        // Ustaw maksymalną liczbę widocznych elementów w dropdown
        courseComboBox.setMaximumRowCount(8);
        courseComboBox.addActionListener(e -> loadGrades());
        panel.add(courseComboBox);
        
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> {
            loadData();
            loadGrades();
        });
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createGradeTablePanel() {
        String[] columnNames = {"Ocena", "Opis", "Typ", "Data", "Litera"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        gradeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Oceny"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGradeFormPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dodaj Nową Ocenę"));
        
        panel.add(new JLabel("Ocena:"));
        gradeSpinner = new JSpinner(new SpinnerNumberModel(3.0, 2.0, 5.0, 0.5));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(gradeSpinner, "0.0");
        gradeSpinner.setEditor(editor);
        panel.add(gradeSpinner);
        
        panel.add(new JLabel("Opis:"));
        descriptionField = new JTextField(15);
        panel.add(descriptionField);
        
        panel.add(new JLabel("Typ:"));
        gradeTypeComboBox = new JComboBox<>(Grade.GradeType.values());
        panel.add(gradeTypeComboBox);
        
        JButton addButton = new JButton("Dodaj Ocenę");
        addButton.addActionListener(e -> addGrade());
        panel.add(addButton);
        
        return panel;
    }
    
    private void loadData() {
        loadStudents();
        loadCourses();
    }
    
    private void loadStudents() {
        studentComboBox.removeAllItems();
        List<Student> students = studentService.getAllStudents();
        
        for (Student student : students) {
            studentComboBox.addItem(student); // ← TU! Automatycznie wywołuje Student.toString()
        }
        
        // Automatycznie wybierz pierwszego studenta jeśli lista nie jest pusta
        if (!students.isEmpty()) {
            studentComboBox.setSelectedIndex(0);
            // Ręcznie wywołaj ładowanie kursów dla pierwszego studenta
            loadCourses();
        }
    }
    
    private void loadCourses() {
        courseComboBox.removeAllItems();
        
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        
        System.out.println("Ładowanie kursów dla studenta: " + 
            (selectedStudent != null ? selectedStudent.getStudentId() : "null"));
        
        if (selectedStudent != null) {
            try {
                List<String> enrolledCourseCodes = studentService.getStudentCourses(selectedStudent.getStudentId());
                System.out.println("Student " + selectedStudent.getStudentId() + " jest zapisany na kursy: " + enrolledCourseCodes);
                
                List<Course> allCourses = courseService.getAllCourses();
                int addedCourses = 0;
                
                for (Course course : allCourses) {
                    if (enrolledCourseCodes.contains(course.getCode())) {
                        courseComboBox.addItem(course);
                        addedCourses++;
                        System.out.println("  Dodano kurs: " + course.getCode() + " - " + course.getName());
                    }
                }
                
                System.out.println("GradePanel: Załadowano " + addedCourses + " kursów dla studenta " + selectedStudent.getStudentId());
                
                // Automatycznie wybierz pierwszy kurs jeśli lista nie jest pusta
                if (addedCourses > 0) {
                    courseComboBox.setSelectedIndex(0);
                } else {
                    System.out.println("UWAGA: Student " + selectedStudent.getStudentId() + " nie ma przypisanych kursów!");
                }
                
            } catch (StudentManagementException e) {
                System.err.println("Błąd ładowania kursów studenta: " + e.getMessage());
            }
        } else {
            System.out.println("GradePanel: Brak wybranego studenta - nie ładuję kursów");
        }
    }
    
    private void loadGrades() {
        tableModel.setRowCount(0);
        
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        System.out.println("Ładowanie ocen dla studenta: " + 
            (selectedStudent != null ? selectedStudent.getStudentId() : "null") + 
            " i kursu: " + (selectedCourse != null ? selectedCourse.getCode() : "null"));
        
        if (selectedStudent != null && selectedCourse != null) {
            List<Grade> grades = selectedStudent.getGrades().get(selectedCourse.getCode());
            System.out.println("Znaleziono " + (grades != null ? grades.size() : 0) + " ocen");
            
            if (grades != null && !grades.isEmpty()) {
                for (Grade grade : grades) {
                    Object[] row = {
                        grade.getValue(),
                        grade.getDescription(),
                        grade.getType().getDisplayName(),
                        grade.getDate(),
                        grade.getGradeLetter()
                    };
                    tableModel.addRow(row);
                    System.out.println("Dodano ocenę do tabeli: " + grade.getValue() + " - " + grade.getDescription());
                }
            } else {
                System.out.println("Brak ocen dla tej kombinacji student-kurs");
            }
        }
    }
    
    private void addGrade() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        if (selectedStudent == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Wybierz studenta i kurs");
            return;
        }
        
        try {
            double value = (Double) gradeSpinner.getValue();
            String description = descriptionField.getText().trim();
            Grade.GradeType type = (Grade.GradeType) gradeTypeComboBox.getSelectedItem();
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wprowadź opis oceny");
                return;
            }
            
            Grade grade = new Grade(value, description, LocalDate.now(), type);
            
            // Sprawdź czy student jest zapisany na kurs
            if (!selectedStudent.getEnrolledCourses().contains(selectedCourse.getCode())) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Student nie jest zapisany na ten kurs. Czy chcesz go zapisać?",
                    "Potwierdzenie", JOptionPane.YES_NO_OPTION);
                    
                if (result == JOptionPane.YES_OPTION) {
                    studentService.enrollStudentInCourse(selectedStudent.getStudentId(), 
                                                       selectedCourse.getCode());
                    courseService.enrollStudentInCourse(selectedCourse.getCode(), 
                                                       selectedStudent.getStudentId());
                } else {
                    return;
                }
            }
            
            studentService.addGradeToStudent(selectedStudent.getStudentId(), 
                                           selectedCourse.getCode(), grade);
            
            loadGrades();
            clearGradeForm();
            
            JOptionPane.showMessageDialog(this, "Ocena dodana pomyślnie");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd dodawania oceny: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearGradeForm() {
        gradeSpinner.setValue(3.0);
        descriptionField.setText("");
        gradeTypeComboBox.setSelectedIndex(0);
    }
    
    public void refreshData() {
        System.out.println("GradePanel: Odświeżanie danych...");
        
        // Zapamiętaj aktualnie wybranego studenta
        Student currentStudent = (Student) studentComboBox.getSelectedItem();
        String currentStudentId = currentStudent != null ? currentStudent.getStudentId() : null;
        
        // Załaduj nowe dane
        loadData();
        
        // Spróbuj przywrócić wybór studenta
        if (currentStudentId != null) {
            for (int i = 0; i < studentComboBox.getItemCount(); i++) {
                Student student = studentComboBox.getItemAt(i);
                if (student.getStudentId().equals(currentStudentId)) {
                    studentComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        loadGrades();
        System.out.println("GradePanel: Dane odświeżone");
    }
}
