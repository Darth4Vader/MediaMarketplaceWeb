package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import backend.dtos.users.LogInDto;

public abstract class BaseAuthenticationSpringTest extends BaseSpringTest {
	
	protected abstract LogInDto getLoginDto();
    
    protected abstract boolean isSetUpIsDone();
    protected abstract String getAuthToken();
    
    protected abstract void setSetUpIsDone(boolean setUpIsDone);
    protected abstract void setAuthToken(String authToken);
    
    
    
    @BeforeEach
    public void setUp() throws Exception {
        if (isSetUpIsDone()) {
            return;
        }
        // do the setup
        setSetUpIsDone(true);
        LogInDto loginDto = getLoginDto();
    	ResultActions a = mockMvc
    			.perform(withJSON(MockMvcRequestBuilders
	    			.post("/api/auth/login")
	    			, loginDto))
    			.andExpect(status().isOk());
    	MvcResult result = a.andReturn();
    	MockHttpServletResponse response = result.getResponse();
    	setAuthToken(response.getContentAsString());
    }

    public MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
    	assertThat(getAuthToken()).as("The auth token is null").isNotNull();
    	return requestBuilder.header("Authorization", getAuthToken());
    }
    
    public MockHttpServletRequestBuilder withJsonAndAuth(MockHttpServletRequestBuilder requestBuilder, Object object) {
    	return withAuth(withJSON(requestBuilder, object));
    }
    
	public ResultActions getWithAuthTest(String uri, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(withAuth(MockMvcRequestBuilders
							.get(uri)))
				.andExpect(matcher);
	}
    
	public ResultActions deleteWithAuthTest(String uri, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(withAuth(MockMvcRequestBuilders
							.delete(uri)))
				.andExpect(matcher);
	}
	
	public ResultActions deleteWithArgsAndAuthTest(ResultMatcher matcher, String uri, Object... uriVariables) throws Exception {
		return mockMvc
				.perform(withAuth(MockMvcRequestBuilders
							.delete(uri, uriVariables)))
				.andExpect(matcher);
	}
	
	public ResultActions postObjectJsonWithAuthTest(String uri, Object object, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(withJsonAndAuth(MockMvcRequestBuilders
							.post(uri),
							object))
				.andExpect(matcher);
	}
}