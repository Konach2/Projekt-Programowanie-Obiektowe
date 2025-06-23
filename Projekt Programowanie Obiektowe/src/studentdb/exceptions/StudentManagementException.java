package studentdb.exceptions;

/**
 * Główna klasa wyjątków dla systemu zarządzania studentami.
 * Demonstruje tworzenie własnych klas wyjątków.
 */
public class StudentManagementException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public StudentManagementException(String message) {
        super(message);
    }
    
    public StudentManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
