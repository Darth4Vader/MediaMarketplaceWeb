package backend;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import backend.exceptions.EmailSendFailedException;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.exceptions.EntityUnprocessableException;
import backend.exceptions.HumanVerificationException;
import backend.exceptions.JwtTokenExpiredException;
import backend.exceptions.JwtTokenNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.MissingCookieException;
import backend.exceptions.MovieReviewValuesAreIncorrectException;
import backend.exceptions.PasswordResetTokenCooldownException;
import backend.exceptions.PurchaseOrderException;
import backend.exceptions.RefreshTokenExpiredException;
import backend.exceptions.RevokedRefreshTokenAccessException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserNotVerifiedException;
import backend.exceptions.UserPasswordIsIncorrectException;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
	
	private final Log LOGGER = LogFactory.getLog(getClass());
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(EntityAdditionException.class)
	public ResponseEntity<Object> handleEntityAdditionException(EntityAdditionException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	@ExceptionHandler(LogValuesAreIncorrectException.class)
	public ResponseEntity<Object> handleLogValuesAreIncorrectException(LogValuesAreIncorrectException ex, WebRequest request) {
		LOGGER.error(ex);
		Map<String, Object> bodyOfResponse = Map.of(
				"error", ex.getMessage(),
				"fields", ex.getUserLogInfo() //ex.getUserLogInfo().stream().map(e -> e.getValue()).toList()
		);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(UserDoesNotExistsException.class)
	public ResponseEntity<Object> handleUserDoesNotExistsException(UserDoesNotExistsException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler(UserPasswordIsIncorrectException.class)
	public ResponseEntity<Object> handleUserPasswordIsIncorrectException(UserPasswordIsIncorrectException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(JwtTokenNotFoundException.class)
	public ResponseEntity<Object> handleJwtTokenNotFoundException(JwtTokenNotFoundException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(JDBCConnectionException.class)
	public ResponseEntity<Object> handleJDBCConnectionException(JDBCConnectionException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, "Unable to connect to Server", new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler(EntityAlreadyExistsException.class)
	public ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(EntityRemovalException.class)
	public ResponseEntity<Object> handleEntityRemovalException(EntityRemovalException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.GONE, request);
	}
	
	@ExceptionHandler(PurchaseOrderException.class)
	public ResponseEntity<Object> handlePurchaseOrderException(PurchaseOrderException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	@ExceptionHandler(MovieReviewValuesAreIncorrectException.class)
	public ResponseEntity<Object> handleMovieReviewValuesAreIncorrectException(MovieReviewValuesAreIncorrectException ex, WebRequest request) {
		LOGGER.error(ex);
		System.out.println(ex.getMessage());
		System.out.println(ex.getMap());
		Map<String, Object> bodyOfResponse = Map.of(
				"error", ex.getMessage(),
				"fields", ex.getMap()
		);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(UserNotLoggedInException.class)
	public ResponseEntity<Object> handleUserNotLoggedInException(UserNotLoggedInException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(RefreshTokenExpiredException.class)
	public ResponseEntity<Object> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(RevokedRefreshTokenAccessException.class)
	public ResponseEntity<Object> handleRevokedRefreshTokenAccessException(RevokedRefreshTokenAccessException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
	}
	
	@ExceptionHandler(JwtTokenExpiredException.class)
	public ResponseEntity<Object> handleJwtTokenExpiredException(JwtTokenExpiredException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(EntityUnprocessableException.class)
	public ResponseEntity<Object> handleEntityUnprocessableException(EntityUnprocessableException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
	}
	
	@ExceptionHandler(MissingCookieException.class)
	public ResponseEntity<Object> handleMissingCookieException(MissingCookieException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(PasswordResetTokenCooldownException.class)
	public ResponseEntity<Object> handlePasswordResetTokenCooldownException(PasswordResetTokenCooldownException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS, request);
	}
	
	@ExceptionHandler(EmailSendFailedException.class)
	public ResponseEntity<Object> handleEmailSendFailedException(EmailSendFailedException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	@ExceptionHandler(UserNotVerifiedException.class)
	public ResponseEntity<Object> handleUserNotVerifiedException(UserNotVerifiedException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
	}
	
	@ExceptionHandler(HumanVerificationException.class)
	public ResponseEntity<Object> handleHumanVerificationException(HumanVerificationException ex, WebRequest request) {
		LOGGER.error(ex);
		Map<String, Object> bodyOfResponse = Map.of(
				"error", ex.getMessage()
		);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	/**
	 * The ExceptionHandler disables the default exception handler
	 * so we will return Internal Server Error for every unhandled exception
	 * @param ex
	 * @return
	 */
    /*@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
    	LOGGER.error(ex);
    	return new ResponseEntity<>("Server encountered an internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}