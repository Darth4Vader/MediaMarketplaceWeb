package backend.exceptions;

public class MissingCookieException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingCookieException() {
		// TODO Auto-generated constructor stub
	}

	public MissingCookieException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public MissingCookieException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public MissingCookieException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MissingCookieException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
