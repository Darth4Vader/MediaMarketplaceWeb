package backend.users;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import backend.BaseSpringTest;

public class LogToUserTest extends BaseSpringTest {
    
    @Test
    public void notLogged() throws Exception {
    	ResultActions a = mockMvc
    			.perform(MockMvcRequestBuilders
    					.get("/api/users/carts/get"))
    			.andExpect(status().isUnauthorized());
    }
}