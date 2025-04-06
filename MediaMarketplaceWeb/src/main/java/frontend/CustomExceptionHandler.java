package frontend;


import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import backend.exceptions.EntityAccessException;
import backend.exceptions.UserNotLoggedInException;
import backend.tmdb.TMDBKeyLoadingException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A custom exception handler that manages uncaught exceptions across threads.
 * It provides specific handling for common exceptions and displays appropriate
 * error messages to the user.
 */
public class CustomExceptionHandler implements UncaughtExceptionHandler {

	/**
	 * The {@code App} instance used for handling navigation or user actions
	 * when an uncaught exception occurs. This instance allows the exception
	 * handler to direct users to login or registration pages if necessary.
	 */
	private App app;
	
    /**
     * Constructs a {@code CustomExceptionHandler} with the specified application instance.
     *
     * @param app the application instance used for navigation or other actions
     */
	public CustomExceptionHandler(App app) {
		this.app = app;
	}
	
    /**
     * Checks if the given throwable or its cause matches the specified exception class.
     *
     * @param caught the throwable to check
     * @param isOfOrCausedBy the exception class to compare against
     * @return {@code true} if the throwable is of the specified class or caused by it; {@code false} otherwise
     */
	private boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
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
	private <T extends Throwable> T getCausedBy(Throwable caught, Class<T> isOfOrCausedBy) {
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
	private <T extends Throwable> T getEqualsCausedBy(Throwable caught, Class<T> isOfOrCausedBy) {
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
	private boolean isThrowableCuaseClassOf(Throwable throwable, String className) {
		if(throwable == null) return false;
		Throwable cause = throwable.getCause();
		if(cause != null) {
			return cause.getClass().getName().equals(className);
		}
		return false;
	}
	
    /**
     * Handles uncaught exceptions by categorizing them and displaying appropriate alerts or logging errors.
     *
     * @param thread the thread where the exception occurred
     * @param throwable the throwable to handle
     */
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Throwable throwable2;
		if(isCausedBy(throwable, UserNotLoggedInException.class)) {
			userNotLogException();
		}
		else if(isCausedBy(throwable, AuthorizationDeniedException.class)) {
			AppUtils.alertOfError("Accsses Deny", "cannot do this operation");
		}
		else if(isCausedBy(throwable, AuthenticationCredentialsNotFoundException.class)) {
			userNotLogException();
		}
		else if(isCausedBy(throwable, JDBCConnectionException.class)) {
			AppUtils.alertOfError("Open Server Error", "Unable to connect to server");
		}
		else if(isCausedBy(throwable, EntityAccessException.class)) {
			//here we handle all the access exception that hibernate returns, but we changed it in the controller level to be a custom runtime exception.
			AppUtils.alertOfError("Accessing data exception", throwable.getMessage());
			throwable.printStackTrace();
		}
		else if(isCausedBy(throwable, GenericJDBCException.class)) {
			//this will notify the user that he didn't install the application properly, incorrect or missing values for the sever username and password.
			throwable = getCausedBy(throwable, GenericJDBCException.class);
			createCustomErrorAlert("Problems With opening the server", throwable);
			throwable.printStackTrace();
		}
		else if((throwable2 = getCausedBy(throwable, BeanInstantiationException.class)) != null
				&& isThrowableCuaseClassOf(throwable2, "org.springframework.boot.autoconfigure.jdbc.DataSourceProperties$DataSourceBeanCreationException")) {
			//this will notify the user that he didn't install the application properly, missing the database url
			Throwable cause = throwable2.getCause();
			String causeMessage = cause.getMessage();
			String message = (causeMessage != null && causeMessage.equals("Failed to determine suitable jdbc url"))
							? "Missing the url of the database" : causeMessage;
			createCustomErrorAlert("Missing server", message);
			cause.printStackTrace();
		}
		else if(isCausedBy(throwable, SQLSyntaxErrorException.class)) {
			//we will notify the user if he the app is using a database that does not exists.
			throwable = getCausedBy(throwable, SQLSyntaxErrorException.class);
			createCustomErrorAlert("Problems With opening the server", throwable);
		}
		else if(isRuntimeExceptionOfDatabaseDriverFailure(throwable)) {
			//we alerted the user that the database url is incorrect (not the needed driver, in this case jdbc,mysql)
			//we did the alert already in the functions, therefore, we don't need to do anything.
		}
		else if((throwable2 = getCausedBy(throwable, TMDBKeyLoadingException.class)) != null) {
			AppUtils.alertOfError("TMDB api key Exception", throwable2.getMessage());
		}
		else {
            String name = "";
            if(thread != null)
            	name = thread.getName();
			System.err.print("Exception in thread \""
                    + name + "\" ");
            throwable.printStackTrace(System.err);
            //we will alert the user that there is an exception that is unspecified.
            AppUtils.alertOfError("Application Exception", "There is an unspecified Problem with the Application");
		}
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
	private boolean isRuntimeExceptionOfDatabaseDriverFailure(Throwable throwable) {
		throwable = getEqualsCausedBy(throwable, RuntimeException.class);
		if(throwable == null) return false;
		String message = throwable.getMessage();
		final String DRIVER = "driver", DATABASE_URL = "url";
		String regex = String.format("^Driver (?<%s>(\\w|.)+) claims to not accept jdbcUrl, (?<%s>(.)+)$", DRIVER, DATABASE_URL);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		if(matcher.matches()) {
			AppUtils.alertOfError("Problems With opening the server driver", "The database url missing drivers");
			return true;
		}
		return false;
	}
	
	/**
	 * Creates and displays a custom error alert with the specified title and the message
	 * from the given throwable.
	 *
	 * <p>This method initializes an error alert using the provided title and the message
	 * extracted from the specified throwable. It delegates to the {@code createCustomErrorAlert}
	 * method that takes a title and a message string.
	 *
	 * @param title the title of the error alert
	 * @param cause the throwable whose message will be used as the content of the error alert
	 * @throws NullPointerException if {@code cause} is {@code null}
	 */
	private void createCustomErrorAlert(String title, Throwable cause) {
		createCustomErrorAlert(title, cause.getMessage());
	}
	
	/**
	 * Creates and displays a custom error alert with the specified title and message.
	 *
	 * <p>This method initializes an error alert with the provided title and message using
	 * the utility method {@code AppUtils.createAlertOfError}. It then sets the width of the
	 * alert to 400 pixels, makes it resizable, and shows it to the user.
	 *
	 * @param title the title of the error alert
	 * @param message the message to be displayed in the error alert
	 */
	private void createCustomErrorAlert(String title, String message) {
		Alert alert = AppUtils.createAlertOfError(title, message);
		alert.setWidth(400);
		alert.setResizable(true);
		alert.show();
	}
	
    /**
     * Displays an alert indicating that the user is not logged in, with options to sign in or register.
     */
	private void userNotLogException() {
		Alert alert = AppUtils.createAlertOfError("The user is not logged in", "sign in or continue to browse as unlogged");
        /*Button signInBtn = createSignButton(alert, true);
        Button registerBtn = createSignButton(alert, false);*/
        HBox box = new HBox();
        box.setSpacing(10);
        //box.getChildren().addAll(signInBtn, registerBtn);
        alert.getDialogPane().setContent(box);
        alert.showAndWait();
        //If the user is not logged in, then we will reset the welcome message.
        if(app != null) {
        	//app.refreshToolBar();
        }
	}
}