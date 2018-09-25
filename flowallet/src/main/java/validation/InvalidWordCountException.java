package validation;

public class InvalidWordCountException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6634989024403314485L;

	public InvalidWordCountException() {
        super("Not a correct number of words");
    }

}
