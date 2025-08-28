package backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigValues {
	
	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	
	public String getFrontendUrl() {
		return frontendUrl;
	}
}
