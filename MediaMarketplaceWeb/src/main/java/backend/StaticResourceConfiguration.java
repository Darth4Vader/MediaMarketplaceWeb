package backend;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String projectDir = Paths.get("").toAbsolutePath().toUri().toString();
    	String imagePath = projectDir + "images/";
    	registry.addResourceHandler("/images/**")
    			.addResourceLocations(imagePath);
    }
}
