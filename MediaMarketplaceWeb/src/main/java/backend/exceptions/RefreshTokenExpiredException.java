package backend.exceptions;

public class RefreshTokenExpiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefreshTokenExpiredException() {
		// TODO Auto-generated constructor stub
	}

	public RefreshTokenExpiredException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RefreshTokenExpiredException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public RefreshTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RefreshTokenExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
