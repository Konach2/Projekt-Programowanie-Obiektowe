import studentdb.gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Główna klasa aplikacji - Baza Danych Studentów
 * System zarządzania informacjami o studentach, kursach i ocenach na uczelni.
 * 
 * Projekt indywidualny - Programowanie Obiektowe
 * Temat: Baza Danych Studentów
 * 
 * @author Mateusz Konachowicz
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Ustawienie Look and Feel dla lepszego wyglądu
        try {
            // Użycie alternatywnego sposobu ustawiania Look and Feel
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            System.err.println("Nie można ustawić systemowego Look and Feel: " + e.getMessage());
            // Używamy domyślnego Look and Feel jeśli nie można ustawić systemowego
        }
        
        // Uruchomienie GUI w wątku Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    System.out.println("Uruchamianie aplikacji: System Zarządzania Bazą Danych Studentów...");
                    
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                    
                    System.out.println("Aplikacja uruchomiona pomyślnie!");
                    
                } catch (Exception e) {
                    System.err.println("Błąd podczas uruchamiania aplikacji: " + e.getMessage());
                    e.printStackTrace();
                    
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Błąd uruchamiania aplikacji:\n" + e.getMessage(),
                        "Błąd", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
