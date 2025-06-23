package studentdb.gui;

import studentdb.models.*;
import studentdb.services.*;
import studentdb.exceptions.StudentManagementException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel zarządzania studentami.
 * Demonstruje użycie Swing komponentów i obsługę zdarzeń.
 */
public class StudentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private StudentService studentService;
    private CourseService courseService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    // Pola formularza
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField birthDateField;
    private JTextField fieldOfStudyField;
    private JSpinner semesterSpinner;
    private JComboBox<Student.StudentType> typeComboBox;
    
    public StudentPanel(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
        initializeGUI();
        loadStudents();
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Panel wyszukiwania i sortowania
        add(createSearchPanel(), BorderLayout.NORTH);
        
        // Tabela studentów
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel formularza
        add(createFormPanel(), BorderLayout.EAST);
        
        // Panel przycisków
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Wyszukiwanie i Sortowanie"));
        
        panel.add(new JLabel("Szukaj:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchStudents());
        panel.add(searchField);
        
        JButton searchButton = new JButton("Szukaj");
        searchButton.addActionListener(e -> searchStudents());
        panel.add(searchButton);
        
        JButton clearButton = new JButton("Wyczyść");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });
        panel.add(clearButton);
        
        panel.add(Box.createHorizontalStrut(20));
        
        JButton sortByIdButton = new JButton("Sortuj po ID");
        sortByIdButton.addActionListener(e -> loadStudents());
        panel.add(sortByIdButton);
        
        JButton sortByNameButton = new JButton("Sortuj po nazwisku");
        sortByNameButton.addActionListener(e -> loadStudentsSortedByName());
        panel.add(sortByNameButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        String[] columnNames = {"ID", "Imię", "Nazwisko", "Email", "Kierunek", "Semestr", "Typ", "Kursy", "Średnia"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista Studentów (sortowana po ID)"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Studenta"));
        panel.setPreferredSize(new Dimension(300, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Inicjalizacja pól
        idField = new JTextField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        emailField = new JTextField(15);
        birthDateField = new JTextField(15);
        birthDateField.setToolTipText("Format: RRRR-MM-DD");
        fieldOfStudyField = new JTextField(15);
        semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        typeComboBox = new JComboBox<>(Student.StudentType.values());
        
        // Dodawanie komponentów
        int row = 0;
        addFormRow(panel, gbc, row++, "ID Studenta:", idField);
        addFormRow(panel, gbc, row++, "Imię:", firstNameField);
        addFormRow(panel, gbc, row++, "Nazwisko:", lastNameField);
        addFormRow(panel, gbc, row++, "Email:", emailField);
        addFormRow(panel, gbc, row++, "Data urodzenia:", birthDateField);
        addFormRow(panel, gbc, row++, "Kierunek:", fieldOfStudyField);
        addFormRow(panel, gbc, row++, "Semestr:", semesterSpinner);
        addFormRow(panel, gbc, row++, "Typ studiów:", typeComboBox);
        
        return panel;
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Dodaj");
        addButton.addActionListener(e -> addStudent());
        panel.add(addButton);
        
        JButton updateButton = new JButton("Aktualizuj");
        updateButton.addActionListener(e -> updateStudent());
        panel.add(updateButton);
        
        JButton deleteButton = new JButton("Usuń");
        deleteButton.addActionListener(e -> deleteStudent());
        panel.add(deleteButton);
        
        JButton clearButton = new JButton("Wyczyść pola");
        clearButton.addActionListener(e -> clearForm());
        panel.add(clearButton);
        
        JButton manageCoursesButton = new JButton("Zarządzaj Kursami");
        manageCoursesButton.addActionListener(e -> openCourseManagementDialog());
        panel.add(manageCoursesButton);
        
        return panel;
    }
    
    private void openCourseManagementDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Wybierz studenta");
            return;
        }
        
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Student student = studentService.getStudent(studentId);
            StudentCourseDialog dialog = new StudentCourseDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                studentService, courseService, student);
            dialog.setVisible(true);
            loadStudents(); // Odśwież po zamknięciu dialogu
        } catch (StudentManagementException e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
    
    private void loadStudents() {
        List<Student> students = studentService.getAllStudents();
        updateTable(students, "sortowana po ID");
    }
    
    private void loadStudentsSortedByName() {
        List<Student> students = studentService.getAllStudentsSortedByName();
        updateTable(students, "sortowana po nazwisku");
    }
    
    private void searchStudents() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadStudents();
            return;
        }
        
        List<Student> students = studentService.searchStudents(query);
        updateTable(students, "wyniki wyszukiwania");
    }
    
    private void updateTable(List<Student> students, String description) {
        tableModel.setRowCount(0);
        
        for (Student student : students) {
            try {
                List<String> courses = studentService.getStudentCourses(student.getStudentId());
                String coursesText = courses.size() > 0 ? 
                    String.join(", ", courses) : "Brak";
                
                Object[] row = {
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getFieldOfStudy(),
                    student.getSemester(),
                    student.getType().getDisplayName(),
                    coursesText,
                    String.format("%.2f", student.calculateGPA())
                };
                tableModel.addRow(row);
            } catch (StudentManagementException e) {
                System.err.println("Błąd ładowania kursów dla studenta: " + e.getMessage());
            }
        }
        
        // Aktualizuj tytuł tabeli
        Component parent = studentTable.getParent().getParent();
        if (parent instanceof JPanel) {
            ((JPanel) parent).setBorder(BorderFactory.createTitledBorder(
                "Lista Studentów (" + description + ")"));
        }
    }
    
    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) tableModel.getValueAt(selectedRow, 0);
            try {
                Student student = studentService.getStudent(studentId);
                fillForm(student);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Błąd ładowania studenta: " + e.getMessage());
            }
        }
    }
    
    private void fillForm(Student student) {
        idField.setText(student.getStudentId());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        birthDateField.setText(student.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        fieldOfStudyField.setText(student.getFieldOfStudy());
        semesterSpinner.setValue(student.getSemester());
        typeComboBox.setSelectedItem(student.getType());
    }
    
    private void addStudent() {
        try {
            String id = idField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            LocalDate birthDate = LocalDate.parse(birthDateField.getText().trim());
            String fieldOfStudy = fieldOfStudyField.getText().trim();
            int semester = (Integer) semesterSpinner.getValue();
            Student.StudentType type = (Student.StudentType) typeComboBox.getSelectedItem();
            
            studentService.addStudent(id, firstName, lastName, email, birthDate, 
                                    fieldOfStudy, semester, type);
            loadStudents();
            clearForm();
            
            JOptionPane.showMessageDialog(this, "Student dodany pomyślnie");
            
            // Automatycznie odśwież inne zakładki
            refreshOtherPanels();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd dodawania studenta: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStudent() {
        try {
            String id = idField.getText().trim();
            Student student = studentService.getStudent(id);
            
            student.setFirstName(firstNameField.getText().trim());
            student.setLastName(lastNameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setBirthDate(LocalDate.parse(birthDateField.getText().trim()));
            student.setFieldOfStudy(fieldOfStudyField.getText().trim());
            student.setSemester((Integer) semesterSpinner.getValue());
            student.setType((Student.StudentType) typeComboBox.getSelectedItem());
            
            studentService.updateStudent(student);
            loadStudents();
            
            JOptionPane.showMessageDialog(this, "Student zaktualizowany pomyślnie");
            
            // Automatycznie odśwież inne zakładki
            refreshOtherPanels();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd aktualizacji studenta: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz studenta do usunięcia");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Czy na pewno chcesz usunąć studenta o ID: " + id + "?", 
            "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                studentService.removeStudent(id);
                loadStudents();
                clearForm();
                JOptionPane.showMessageDialog(this, "Student usunięty pomyślnie");
                
                // Automatycznie odśwież inne zakładki
                refreshOtherPanels();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Błąd usuwania studenta: " + e.getMessage(), 
                                            "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshOtherPanels() {
        // Powiadom główne okno o zmianie danych
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame instanceof MainFrame) {
            ((MainFrame) parentFrame).refreshAllPanels();
        }
    }
    
    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        birthDateField.setText("");
        fieldOfStudyField.setText("");
        semesterSpinner.setValue(1);
        typeComboBox.setSelectedIndex(0);
    }
    
    public void refreshData() {
        loadStudents();
    }
}
