package backend.tmdb;

/**
 * Exception thrown to indicate that an error occurred while loading the TMDB API key.
 */
public class TMDBKeyLoadingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TMDBKeyLoadingException() {
		// TODO Auto-generated constructor stub
	}

	public TMDBKeyLoadingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TMDBKeyLoadingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public TMDBKeyLoadingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TMDBKeyLoadingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
