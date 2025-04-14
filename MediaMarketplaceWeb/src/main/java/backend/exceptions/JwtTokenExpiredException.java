package backend.exceptions;

public class JwtTokenExpiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JwtTokenExpiredException() {
		// TODO Auto-generated constructor stub
	}

	public JwtTokenExpiredException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenExpiredException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JwtTokenExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
