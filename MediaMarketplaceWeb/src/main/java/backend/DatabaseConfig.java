package backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.MySqlJdbcIndexedSessionRepositoryCustomizer;

@Configuration
public class DatabaseConfig {
	
	@Bean
	public MySqlJdbcIndexedSessionRepositoryCustomizer sessionRepositoryCustomizer() {
	    return new MySqlJdbcIndexedSessionRepositoryCustomizer();
	}
	
}