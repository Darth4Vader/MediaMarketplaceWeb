package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import backend.entities.enums.RoleType;
import backend.services.UserAuthenticateService;

/**
 * The main entry point of the Spring Boot application.
 * <p>
 * This class is responsible for setting up the application context and performing initialization tasks at startup.
 * It also ensures that necessary configurations and services are loaded.
 * </p>
 */
@PropertySource(value = "application.properties")
@ComponentScan({"backend", "frontend"})
@EntityScan("backend.entities")
@EnableJpaRepositories(basePackages = "backend.repositories")
@EnableJpaAuditing
@SpringBootApplication
public class ActivateSpringApplication implements CommandLineRunner {
	
	public static final String IMAGES_FOLDER = "images";

    @Autowired
    private UserAuthenticateService userAuthenticateService;

    /**
     * Creates and runs the Spring application context.
     * <p>
     * This method is called to bootstrap the application. It initializes the Spring context and starts the application.
     * </p>
     * 
     * @param args command line arguments
     * @return the application context
     */
    public static ConfigurableApplicationContext create(String... args) {
        try {
        	return SpringApplication.run(ActivateSpringApplication.class, args);
        }
        catch(Exception exp) {
        	if(!ActivateSpringExceptionHandler.handleException(exp))
        		throw exp;
        	return null;
        }
    }

    /**
     * Initializes the application by loading roles into the system.
     * <p>
     * This method is executed after the application context is fully initialized.
     * It retrieves and ensures that all role types are present in the system.
     * </p>
     * 
     * @param args command line arguments
     * @throws Exception if any error occurs during initialization
     */
    @Override
    public void run(String... args) throws Exception {
        RoleType[] roleTypes = RoleType.values();
        for (RoleType roleType : roleTypes) {
            userAuthenticateService.getRoleByType(roleType);
        }
    }
}