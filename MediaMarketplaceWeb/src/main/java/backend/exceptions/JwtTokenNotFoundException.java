package backend.exceptions;

public class JwtTokenNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JwtTokenNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public JwtTokenNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
