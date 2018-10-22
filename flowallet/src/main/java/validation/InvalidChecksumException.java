package validation;

public class InvalidChecksumException extends Exception
{
	
	    /**
	 * 
	 */
	private static final long serialVersionUID = -1678707613538273604L;

		public InvalidChecksumException() {
	        super("Invalid checksum");
	    }
}
