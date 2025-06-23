package studentdb.exceptions;

/**
 * Wyjątek rzucany gdy kurs nie zostanie znaleziony.
 */
public class CourseNotFoundException extends StudentManagementException {
    private static final long serialVersionUID = 1L;
    
    public CourseNotFoundException(String courseCode) {
        super("Kurs o kodzie '" + courseCode + "' nie został znaleziony");
    }
}
