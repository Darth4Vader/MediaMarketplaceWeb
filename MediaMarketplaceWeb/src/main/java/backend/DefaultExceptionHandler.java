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

import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.exceptions.JwtTokenNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.MovieReviewValuesAreIncorrectException;
import backend.exceptions.PurchaseOrderException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
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
				"values_problems", ex.getUserLogInfo().stream().map(Enum::name).toList()
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
		Map<String, Object> bodyOfResponse = Map.of(
				"error", ex.getMessage(),
				"values_problems", ex.getMap()
		);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(UserNotLoggedInException.class)
	public ResponseEntity<Object> handleUserNotLoggedInException(UserNotLoggedInException ex, WebRequest request) {
		LOGGER.error(ex);
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
}