package studentdb.gui;

import studentdb.services.*;
import studentdb.utils.FileManager;
import studentdb.utils.SampleDataLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Główne okno aplikacji - Baza Danych Studentów.
 * Demonstruje użycie Swing GUI i organizację interfejsu użytkownika.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private StudentService studentService;
    private CourseService courseService;
    private JTabbedPane tabbedPane;
    
    public MainFrame() {
        initializeServices();
        initializeGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System Zarządzania Bazą Danych Studentów");
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initializeServices() {
        FileManager fileManager = new FileManager();
        this.studentService = new StudentService(fileManager);
        this.courseService = new CourseService(fileManager);
        
        // Zawsze załaduj dane przykładowe przy starcie
        System.out.println("Ładowanie danych przykładowych...");
        SampleDataLoader.loadSampleData(studentService, courseService);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Menu bar
        setJMenuBar(createMenuBar());
        
        // Tabs
        tabbedPane = new JTabbedPane();
        
        StudentPanel studentPanel = new StudentPanel(studentService, courseService);
        CoursePanel coursePanel = new CoursePanel(courseService, studentService);
        GradePanel gradePanel = new GradePanel(studentService, courseService);
        
        tabbedPane.addTab("Studenci", new ImageIcon(), studentPanel, "Zarządzanie studentami");
        tabbedPane.addTab("Kursy", new ImageIcon(), coursePanel, "Zarządzanie kursami");
        tabbedPane.addTab("Oceny", new ImageIcon(), gradePanel, "Zarządzanie ocenami");
        
        // Dodaj listener do odświeżania danych przy zmianie zakładki
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            System.out.println("Zmieniono zakładkę na: " + selectedIndex);
            
            // Odśwież dane w aktualnie wybranej zakładce
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof GradePanel) {
                System.out.println("Odświeżam dane w zakładce Oceny");
                ((GradePanel) selectedComponent).refreshData();
            } else if (selectedComponent instanceof StudentPanel) {
                System.out.println("Odświeżam dane w zakładce Studenci");
                ((StudentPanel) selectedComponent).refreshData();
            } else if (selectedComponent instanceof CoursePanel) {
                System.out.println("Odświeżam dane w zakładce Kursy");
                ((CoursePanel) selectedComponent).refreshData();
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusBar = new JLabel("Gotowy - System zarządzania studentami i kursami");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Plik
        JMenu fileMenu = new JMenu("Plik");
        fileMenu.setMnemonic('P');
        
        JMenuItem loadSampleDataItem = new JMenuItem("Załaduj przykładowe dane");
        loadSampleDataItem.setMnemonic('Z');
        loadSampleDataItem.addActionListener(e -> loadSampleDataManually());
        fileMenu.add(loadSampleDataItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Wyjście");
        exitItem.setMnemonic('W');
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu Pomoc
        JMenu helpMenu = new JMenu("Pomoc");
        helpMenu.setMnemonic('o');
        
        JMenuItem aboutItem = new JMenuItem("O programie");
        aboutItem.setMnemonic('O');
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private void loadSampleDataManually() {
        int result = JOptionPane.showConfirmDialog(this,
            "Czy na pewno chcesz załadować nowe przykładowe dane?\n" +
            "To USUNIE wszystkie istniejące dane i utworzy nowe!",
            "UWAGA - Usunięcie danych", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                System.out.println("Użytkownik wymusza ponowne ładowanie danych...");
                SampleDataLoader.loadSampleData(studentService, courseService);
                refreshAllPanels();
                JOptionPane.showMessageDialog(this, 
                    "Nowe przykładowe dane zostały załadowane!\n" +
                    "Studentów: 6 (S001-S006)\n" +
                    "Kursów: 5\n" +
                    "Każdy student zapisany na inne kursy.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Błąd podczas ładowania danych: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Zmieniono z private na public żeby StudentPanel mógł wywołać
    public void refreshAllPanels() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component component = tabbedPane.getComponentAt(i);
            if (component instanceof StudentPanel) {
                ((StudentPanel) component).refreshData();
            } else if (component instanceof CoursePanel) {
                ((CoursePanel) component).refreshData();
            } else if (component instanceof GradePanel) {
                ((GradePanel) component).refreshData();
            }
        }
    }
    
    private void showAboutDialog() {
        String message = "System Zarządzania Bazą Danych Studentów\n\n" +
                        "Wersja 1.0\n" +
                        "Programowanie Obiektowe - Projekt Indywidualny\n\n" +
                        "Funkcjonalności:\n" +
                        "• Zarządzanie studentami (dodawanie, edycja, usuwanie)\n" +
                        "• Zarządzanie kursami\n" +
                        "• System ocen z różnymi typami\n" +
                        "• Automatyczne ładowanie danych testowych\n" +
                        "• Persystencja danych w plikach\n\n" +
                        "Autor: [Twoje Imię i Nazwisko]";
        
        JOptionPane.showMessageDialog(this, message, "O programie", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
}
