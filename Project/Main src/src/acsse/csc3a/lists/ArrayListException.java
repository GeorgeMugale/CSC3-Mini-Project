package acsse.csc3a.lists;

/**
 * An exception thrown by an arraylist
 */
public class ArrayListException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the exception
	 */
	public ArrayListException() {
        super();
    }
    
    /**
     * Constructs the exception with a message
     * @param message
     */
    public ArrayListException(String message) {
        super(message);
    }
    
    /**
     * Constructs the exception with a given message and cause
     * @param message
     * @param cause
     */
    public ArrayListException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs the exception with a given cause
     * @param cause
     */
    public ArrayListException(Throwable cause) {
        super(cause);
    } 
	

}
