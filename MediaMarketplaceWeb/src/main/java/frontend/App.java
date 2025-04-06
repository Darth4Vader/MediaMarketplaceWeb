package frontend;
import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import backend.ActivateSpringApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Main application class for managing the JavaFX application lifecycle and user interface transitions.
 * Handles initialization, scene management, and user authentication interactions.
 * 
 * <p>This is the class we need to start in order to activate the MarketPlace Spring server and JavaFX app.</p>
 */
@Component
public class App extends Application {
	
	//use this if activate with JavaFX components
	//--module-path "C:\JavaFX_22.02\lib" --add-modules javafx.controls,javafx.fxml
    
    /**
     * The Spring application context used for dependency injection and managing Spring beans.
     * 
     * <p>This is a class parameter.</p>
     */
    private ConfigurableApplicationContext appContext;
    
    /**
     * The singleton instance of the application.
     */
    private static App applicationInstance;

    /**
     * The primary stage for this application.
     */
    private Stage stage;

    /**
     * Retrieves the singleton instance of the application.
     * <p>This method provides access to the single instance of the application, allowing other parts of the code
     * to reference the current running instance of the application.</p>
     * 
     * @return The singleton instance of the application.
     */
    public static App getApplicationInstance() {
        return applicationInstance;
    }

    /**
     * Main entry point for the JavaFX application. Launches the application.
     * <p>This method initializes and launches the JavaFX application. It serves as the entry point for the application,
     * handling command-line arguments if necessary.</p>
     * 
     * @param args Command line arguments.
     * @throws Exception If an error occurs during launch.
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * Initializes the application, setting up exception handling and the Spring context.
     * <p>This method sets up exception handling for the application and initializes the Spring application context.
     * It is called before the JavaFX application starts.</p>
     * 
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
    	// We will set the uncaught exception handler to a custom handler
        // in order to handle certain exception types with JavaFX
        // For example: caught every user operation a guest tries to activate
        Thread curThread = Thread.currentThread();
        curThread.setUncaughtExceptionHandler(new CustomExceptionHandler(this));
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        try {
            applicationInstance = this;
            String[] args = getParameters().getRaw().toArray(new String[0]);
            appContext = ActivateSpringApplication.create(args);
        } catch (Exception e) {
        	Thread thread = Thread.currentThread();
            Platform.runLater(() -> Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, e));
        }
    }

    /**
     * Starts the JavaFX application, setting the primary stage and loading the home page.
     * <p>This method is called after initialization. It sets up the primary stage, configures the application icon,
     * and loads the initial home page for display.</p>
     * 
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        //stage.show();
    }

    /**
     * Stops the JavaFX application and closes the Spring context.
     * <p>This method is called when the application is stopped. It closes the Spring application context and exits
     * the JavaFX platform.</p>
     */
    @Override
    public void stop() {
        // When stopping the market app, close the rest application and the JavaFX application
        if (appContext != null) {
            appContext.close();
        }
        Platform.exit();
    }

    /**
     * Gets an FXMLLoader instance configured with the Spring application context.
     * <p>This method creates an FXMLLoader instance and sets its controller factory to use the Spring application
     * context for dependency injection.</p>
     * 
     * @param fxmlPath Path to the FXML file.
     * @return The configured FXMLLoader instance.
     */
    public FXMLLoader getFXMLLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(appContext::getBean);
        return loader;
    }

    /**
     * Loads a Parent node using the specified FXML path.
     * <p>This method creates an FXMLLoader for the specified FXML file path and loads the corresponding Parent node.</p>
     * 
     * @param fxmlPath Path to the FXML file.
     * @return The loaded Parent node, or null if an error occurs.
     */
    public Parent loadFXML(String fxmlPath) {
        FXMLLoader loader = getFXMLLoader(fxmlPath);
        return loadFXML(loader);
    }

    /**
     * Loads a Parent node using the given FXMLLoader.
     * <p>This method attempts to load a Parent node from the provided FXMLLoader instance. If an error occurs, it
     * is handled by the custom exception handler.</p>
     * 
     * @param loader The FXMLLoader instance.
     * @return The loaded Parent node, or null if an error occurs.
     */
    private Parent loadFXML(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            // If there is an exception with loading the FXML then send an exception to our custom handler, and return null
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            return null;
        }
    }

    /**
     * Gets the Spring application context.
     * <p>This method returns the Spring application context, which provides access to Spring-managed beans and services.</p>
     * 
     * @return The application context.
     */
    public ConfigurableApplicationContext getContext() {
        return this.appContext;
    }

    /**
     * Gets the primary stage of the application.
     * <p>This method returns the primary stage of the JavaFX application, which is used to manage the main window.</p>
     * 
     * @return The primary stage.
     */
    public Stage getStage() {
        return this.stage;
    }
}