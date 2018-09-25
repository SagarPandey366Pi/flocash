package validation;

public class UnexpectedWhiteSpaceException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2209426913570984479L;

	public UnexpectedWhiteSpaceException() {
        super("Unexpected whitespace");
    }
}
