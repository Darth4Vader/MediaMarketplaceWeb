package backend.users;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import backend.UnknownSpringTest;

public class RecommendationsTest extends UnknownSpringTest {
	
    @Test
    public void notLogged() throws Exception {
    	getTest("/api/users/carts/", status().isUnauthorized());
    }
}