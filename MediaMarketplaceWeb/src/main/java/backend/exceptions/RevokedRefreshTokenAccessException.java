package backend.exceptions;

public class RevokedRefreshTokenAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RevokedRefreshTokenAccessException(String message) {
		super(message);
	}

	public RevokedRefreshTokenAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public RevokedRefreshTokenAccessException(Throwable cause) {
		super(cause);
	}

}
