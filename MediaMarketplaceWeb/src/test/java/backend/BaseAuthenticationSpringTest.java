package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import backend.dtos.users.LogInDto;

public class BaseAuthenticationSpringTest extends BaseSpringTest {
	
    private static boolean setUpIsDone = false;
    
    private static String authToken;
    
    @BeforeEach
    public void setUp() throws Exception {
        if (setUpIsDone) {
            return;
        }
        // do the setup
        setUpIsDone = true;
        LogInDto loginDto = new LogInDto();
        loginDto.setUsername("frodo");
        loginDto.setPassword("bag");
    	ResultActions a = mockMvc
    			.perform(withJSON(MockMvcRequestBuilders
	    			.post("/api/auth/login")
	    			, loginDto))
    			.andExpect(status().isOk());
    	MvcResult result = a.andReturn();
    	MockHttpServletResponse response = result.getResponse();
        authToken = response.getContentAsString();
    }

    public MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
    	assertThat(authToken).as("The auth token is null").isNotNull();
    	return requestBuilder.header("Authorization", authToken);
    }
    
    public MockHttpServletRequestBuilder withJsonAndAuth(MockHttpServletRequestBuilder requestBuilder, Object object) {
    	return withAuth(withJSON(requestBuilder, object));
    }
}