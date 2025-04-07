package backend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.hibernate.exception.JDBCConnectionException;

public class ActivateSpringExceptionHandler {
	
	private static final Log LOGGER = org.apache.commons.logging.LogFactory.getLog(ActivateSpringExceptionHandler.class);
	
    /**
     * Checks if the given throwable or its cause matches the specified exception class.
     *
     * @param caught the throwable to check
     * @param isOfOrCausedBy the exception class to compare against
     * @return {@code true} if the throwable is of the specified class or caused by it; {@code false} otherwise
     */
	private static boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
		return getCausedBy(caught, isOfOrCausedBy) != null;
	}
	
	/**
	 * Searches for the first occurrence of a throwable in the cause chain that is
	 * assignable to the specified exception class.
	 *
	 * <p>This method traverses the cause chain of the given throwable and returns
	 * the first throwable that is an instance of or assignable to the specified
	 * exception class. If no such throwable is found, it returns {@code null}.
	 *
	 * @param <T> the type of throwable to find
	 * @param caught the throwable to start the search from
	 * @param isOfOrCausedBy the exception class to compare against
	 * @return the first throwable in the cause chain that is assignable to the specified
	 *         exception class, or {@code null} if no such throwable is found
	 */
	private static <T extends Throwable> T getCausedBy(Throwable caught, Class<T> isOfOrCausedBy) {
		if (caught == null || isOfOrCausedBy == null) return null;
		else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return isOfOrCausedBy.cast(caught);
	    else return getCausedBy(caught.getCause(), isOfOrCausedBy);
	}
	
	/**
	 * Searches for the first occurrence of a throwable in the cause chain that is
	 * equal to the specified exception class.
	 *
	 * <p>This method traverses the cause chain of the given throwable and returns
	 * the first throwable whose class is equal to the specified exception class.
	 * If no such throwable is found, it returns {@code null}.
	 *
	 * @param <T> the type of throwable to find
	 * @param caught the throwable to start the search from
	 * @param isOfOrCausedBy the exception class to compare against
	 * @return the first throwable in the cause chain whose class is equal to the specified
	 *         exception class, or {@code null} if no such throwable is found
	 */
	private static <T extends Throwable> T getEqualsCausedBy(Throwable caught, Class<T> isOfOrCausedBy) {
		if (caught == null || isOfOrCausedBy == null) return null;
		else if (isOfOrCausedBy.equals(caught.getClass())) return isOfOrCausedBy.cast(caught);
	    else return getEqualsCausedBy(caught.getCause(), isOfOrCausedBy);
	}
	
	/**
	 * Checks if the cause of the given throwable has a class with the specified name.
	 *
	 * <p>This method examines the cause of the provided throwable and determines
	 * whether the class of the cause matches the specified class name. If the cause
	 * is {@code null} or if no cause exists, the method returns {@code false}.
	 *
	 * @param throwable the throwable whose cause is to be checked
	 * @param className the fully qualified name of the class to compare against
	 * @return {@code true} if the cause of the throwable has a class with the specified name;
	 *         {@code false} otherwise
	 */
	private static boolean isThrowableCuaseClassOf(Throwable throwable, String className) {
		if(throwable == null) return false;
		Throwable cause = throwable.getCause();
		if(cause != null) {
			return cause.getClass().getName().equals(className);
		}
		return false;
	}
	
	public static boolean handleException(Throwable throwable) {
		if(isCausedBy(throwable, JDBCConnectionException.class)) {
			LOGGER.error("Open Server Error: Unable to connect to server");
		}
		else if(isRuntimeExceptionOfDatabaseDriverFailure(throwable)) {
			//we alerted the user that the database url is incorrect (not the needed driver, in this case jdbc,mysql)
			LOGGER.error("Problems With opening the server driver: The database url missing drivers");
		}
		else {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the given throwable is a {@code RuntimeException} indicating a database driver failure.
	 *
	 * <p>This method first attempts to find a {@code RuntimeException} in the cause chain of the
	 * provided throwable. It then examines the message of this {@code RuntimeException} to determine
	 * if it matches a specific pattern that indicates a database driver failure. If a match is found,
	 * it displays an error alert indicating issues with the database driver and returns {@code true}.
	 * Otherwise, it returns {@code false}.
	 *
	 * <p>The expected message pattern is: "Driver <driver> claims to not accept jdbcUrl, <url>", where
	 * <driver> and <url> are placeholders for actual values.
	 *
	 * @param throwable the throwable to check
	 * @return {@code true} if the throwable is a {@code RuntimeException} indicating a database driver
	 *         failure; {@code false} otherwise
	 */
	private static boolean isRuntimeExceptionOfDatabaseDriverFailure(Throwable throwable) {
		throwable = getEqualsCausedBy(throwable, RuntimeException.class);
		if(throwable == null) return false;
		String message = throwable.getMessage();
		final String DRIVER = "driver", DATABASE_URL = "url";
		String regex = String.format("^Driver (?<%s>(\\w|.)+) claims to not accept jdbcUrl, (?<%s>(.)+)$", DRIVER, DATABASE_URL);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			return true;
		}
		return false;
	}
}