package frontend;


import java.io.File;
import java.net.URI;
import java.net.URL;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Utility class for managing image resources in the application.
 * <p>Provides methods to load images from various sources and set application icons.</p>
 */
public class AppImageUtils {

    /**
     * Loads an image from the specified path, either from a URL or the file system.
     * <p>This method attempts to load an image using a URL first. If that fails, it attempts to load the image
     * from the file system. If an exception occurs during the loading process, it returns <code>null</code>.</p>
     * 
     * @param path The path to the image resource. It can be a URL or a file system path.
     * @return The loaded {@link Image} object, or <code>null</code> if an error occurs.
     */
    public static Image loadImageFromClass(String path) {
        try {
            URL url;
            try {
                // Try to read the image path from a URL
                url = URI.create(path).toURL();
            } catch (Exception e) {
                // If fails, then try to read the path from the file system
                url = new File(path).toURI().toURL();
            }
            String fullPath = url.toExternalForm();
            return new Image(fullPath, true);
        } catch (Exception e) {
            // If there is an exception with loading the image, or it is missing then return null
            return null;
        }
    }

    /**
     * The application icon image used for setting the icon on the application stage.
     * <p>This field is initialized the first time {@link #loadAppIconImage(Stage)} is called.</p>
     */
    private static Image iconImage;

    /**
     * The name of the application icon image file.
     * <p>Assumed to be located in the resources directory.</p>
     */
    private static final String ICON_NAME = "markplace_logo.png";

    /**
     * Loads and sets the application icon image for the given stage.
     * <p>This method loads the application icon image from the resources if it has not been loaded yet.
     * It then sets this image as the icon for the provided {@link Stage}.</p>
     * 
     * @param stage The {@link Stage} to set the application icon on.
     */
    public static void loadAppIconImage(Stage stage) {
        if (iconImage == null) {
            iconImage = new Image(AppImageUtils.class.getResourceAsStream(ICON_NAME));
        }
        stage.getIcons().add(iconImage);
    }
}