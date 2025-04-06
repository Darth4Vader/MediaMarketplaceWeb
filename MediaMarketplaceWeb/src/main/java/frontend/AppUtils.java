package frontend;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Utility class for common application functions related to UI operations.
 * Provides methods for creating alerts and managing the display of movie data in a list view.
 */
public class AppUtils {
	
    /**
     * Creates an error alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} of type {@link AlertType#ERROR} configured with the specified title and body text
     */
	public static Alert createAlertOfError(String title, String bodyText) {
		return createAlertOfType(AlertType.ERROR, title, bodyText);
	}
	
    /**
     * Shows an error alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return the {@link Alert} that was displayed
     */
	public static Alert alertOfError(String title, String bodyText) {
		Alert alert = createAlertOfError(title, bodyText);
		alert.show();
		return alert;
	}
	
    /**
     * Creates an informational alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} of type {@link AlertType#INFORMATION} configured with the specified title and body text
     */
	public static Alert createAlertOfInformation(String title, String bodyText) {
		return createAlertOfType(AlertType.INFORMATION, title, bodyText);
	}
	
    /**
     * Shows an informational alert with the specified title and body text.
     * 
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return the {@link Alert} that was displayed
     */
	public static Alert alertOfInformation(String title, String bodyText) {
		Alert alert = createAlertOfInformation(title, bodyText);
		alert.show();
		return alert;
	}
	
    /**
     * Creates an alert of the specified type with the given title and body text.
     * 
     * @param type the type of alert to create (e.g., {@link AlertType#ERROR} or {@link AlertType#INFORMATION})
     * @param title the title of the alert
     * @param bodyText the body text of the alert
     * @return an {@link Alert} configured with the specified type, title, and body text
     */
	public static Alert createAlertOfType(AlertType type, String title, String bodyText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(bodyText);
		//set the alert icon to our marketplace icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        AppImageUtils.loadAppIconImage(stage);
        return alert;
	}
}