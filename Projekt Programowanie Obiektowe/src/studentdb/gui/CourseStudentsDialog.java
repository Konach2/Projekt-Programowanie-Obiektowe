package studentdb.gui;

import studentdb.models.Course;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Dialog pokazujący listę studentów zapisanych na kurs.
 */
public class CourseStudentsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Course course;
    
    public CourseStudentsDialog(JFrame parent, Course course) {
        super(parent, "Studenci na kursie: " + course.getName(), true);
        this.course = course;
        
        initializeGUI();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Informacje o kursie
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informacje o kursie"));
        
        infoPanel.add(new JLabel("Kod kursu:"));
        infoPanel.add(new JLabel(course.getCode()));
        
        infoPanel.add(new JLabel("Nazwa:"));
        infoPanel.add(new JLabel(course.getName()));
        
        infoPanel.add(new JLabel("ECTS:"));
        infoPanel.add(new JLabel(String.valueOf(course.getCredits())));
        
        infoPanel.add(new JLabel("Zapisanych studentów:"));
        infoPanel.add(new JLabel(course.getEnrolledStudents().size() + " / " + course.getMaxStudents()));
        
        infoPanel.add(new JLabel("Wolne miejsca:"));
        infoPanel.add(new JLabel(String.valueOf(course.getAvailableSpots())));
        
        add(infoPanel, BorderLayout.NORTH);
        
        // Lista studentów
        Set<String> enrolledStudents = course.getEnrolledStudents();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        if (enrolledStudents.isEmpty()) {
            listModel.addElement("Brak zapisanych studentów");
        } else {
            for (String studentId : enrolledStudents) {
                listModel.addElement(studentId);
            }
        }
        
        JList<String> studentsList = new JList<>(listModel);
        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(studentsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista zapisanych studentów"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Przycisk zamknij
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
