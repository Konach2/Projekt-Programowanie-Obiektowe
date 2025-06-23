package studentdb.exceptions;

/**
 * Wyjątek rzucany gdy student nie zostanie znaleziony.
 */
public class StudentNotFoundException extends StudentManagementException {
    private static final long serialVersionUID = 1L;
    
    public StudentNotFoundException(String studentId) {
        super("Student o ID '" + studentId + "' nie został znaleziony");
    }
}
