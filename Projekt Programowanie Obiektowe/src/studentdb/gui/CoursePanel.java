package studentdb.gui;

import studentdb.models.Course;
import studentdb.services.CourseService;
import studentdb.services.StudentService;
import studentdb.exceptions.StudentManagementException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel zarządzania kursami.
 * Demonstruje użycie Swing komponentów do zarządzania kursami.
 */
public class CoursePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private CourseService courseService;
    private StudentService studentService;
    private JList<Course> courseList;
    private DefaultListModel<Course> listModel;
    
    // Pola formularza
    private JTextField codeField;
    private JTextField nameField;
    private JTextField descField;
    private JTextField creditsField;
    private JTextField maxStudentsField;
    
    public CoursePanel(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
        initializeGUI();
        
        // Dodaj opóźnienie i wymuś odświeżenie
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadCourses();
                System.out.println("CoursePanel: Załadowano " + listModel.getSize() + " kursów");
            }
        });
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Lista kursów
        listModel = new DefaultListModel<Course>();
        courseList = new JList<Course>(listModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel formularza
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.EAST);
        
        // Przyciski
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Listener dla listy
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCourse();
            }
        });
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Kursu"));
        panel.setPreferredSize(new Dimension(250, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        codeField = new JTextField(15);
        nameField = new JTextField(15);
        descField = new JTextField(15);
        creditsField = new JTextField(15);
        maxStudentsField = new JTextField(15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Kod:"), gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nazwa:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Opis:"), gbc);
        gbc.gridx = 1;
        panel.add(descField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("ECTS:"), gbc);
        gbc.gridx = 1;
        panel.add(creditsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Max studentów:"), gbc);
        gbc.gridx = 1;
        panel.add(maxStudentsField, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Dodaj Kurs");
        addButton.addActionListener(e -> addCourse());
        panel.add(addButton);
        
        JButton updateButton = new JButton("Aktualizuj");
        updateButton.addActionListener(e -> updateCourse());
        panel.add(updateButton);
        
        JButton deleteButton = new JButton("Usuń");
        deleteButton.addActionListener(e -> deleteCourse());
        panel.add(deleteButton);
        
        JButton clearButton = new JButton("Wyczyść");
        clearButton.addActionListener(e -> clearForm());
        panel.add(clearButton);
        
        JButton viewStudentsButton = new JButton("Zobacz Studentów");
        viewStudentsButton.addActionListener(e -> viewStudentsInCourse());
        panel.add(viewStudentsButton);
        
        return panel;
    }
    
    private void viewStudentsInCourse() {
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Wybierz kurs");
            return;
        }
        
        CourseStudentsDialog dialog = new CourseStudentsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            selectedCourse);
        dialog.setVisible(true);
    }
    
    private void addCourse() {
        try {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            int credits = Integer.parseInt(creditsField.getText().trim());
            int maxStudents = Integer.parseInt(maxStudentsField.getText().trim());
            
            courseService.addCourse(code, name, desc, credits, maxStudents);
            loadCourses();
            clearForm();
            
            JOptionPane.showMessageDialog(this, "Kurs dodany pomyślnie");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowy format liczby", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (StudentManagementException e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCourse() {
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Wybierz kurs do aktualizacji");
            return;
        }
        
        try {
            selectedCourse.setCode(codeField.getText().trim());
            selectedCourse.setName(nameField.getText().trim());
            selectedCourse.setDescription(descField.getText().trim());
            selectedCourse.setCredits(Integer.parseInt(creditsField.getText().trim()));
            selectedCourse.setMaxStudents(Integer.parseInt(maxStudentsField.getText().trim()));
            
            courseService.updateCourse(selectedCourse);
            loadCourses();
            
            JOptionPane.showMessageDialog(this, "Kurs zaktualizowany pomyślnie");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowy format liczby", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (StudentManagementException e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCourse() {
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Wybierz kurs do usunięcia");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Czy na pewno chcesz usunąć kurs: " + selectedCourse.getName() + "?", 
            "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                courseService.removeCourse(selectedCourse.getCode());
                loadCourses();
                clearForm();
                JOptionPane.showMessageDialog(this, "Kurs usunięty pomyślnie");
            } catch (StudentManagementException e) {
                JOptionPane.showMessageDialog(this, "Błąd usuwania kursu: " + e.getMessage(), 
                                            "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadSelectedCourse() {
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null) {
            codeField.setText(selectedCourse.getCode());
            nameField.setText(selectedCourse.getName());
            descField.setText(selectedCourse.getDescription());
            creditsField.setText(String.valueOf(selectedCourse.getCredits()));
            maxStudentsField.setText(String.valueOf(selectedCourse.getMaxStudents()));
        }
    }
    
    private void loadCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            listModel.clear();
            
            System.out.println("Ładowanie " + courses.size() + " kursów do listy");
            
            for (Course course : courses) {
                listModel.addElement(course);
                System.out.println("Dodano kurs: " + course.toString());
            }
            
            if (courses.isEmpty()) {
                System.out.println("UWAGA: Lista kursów jest pusta!");
            }
            
        } catch (Exception e) {
            System.err.println("Błąd ładowania kursów: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd ładowania kursów: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        descField.setText("");
        creditsField.setText("");
        maxStudentsField.setText("");
    }
    
    public void refreshData() {
        loadCourses();
    }
}
