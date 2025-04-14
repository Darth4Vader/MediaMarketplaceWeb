package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import backend.dtos.users.LogInDto;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.RefreshTokenRequest;

public abstract class BaseAuthenticationSpringTest extends BaseSpringTest {
	
	protected abstract LogInDto getLoginDto();
    
    protected abstract boolean isSetUpIsDone();
    protected abstract String getAccessToken();
    protected abstract String getRefreshToken();
    
    protected abstract void setSetUpIsDone(boolean setUpIsDone);
    protected abstract void setAccessToken(String accessToken);
    protected abstract void setRefreshToken(String refreshToken);
    
    @BeforeEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void setUp() throws Exception {
        if (isSetUpIsDone()) {
            return;
        }
        // do the setup
        setSetUpIsDone(true);
        LogInDto loginDto = getLoginDto();
    	ResultActions a = mockMvc
    			.perform(withJSON(MockMvcRequestBuilders
	    			.post("/api/users/login")
	    			, loginDto))
    			.andExpect(status().isOk());
    	MvcResult result = a.andReturn();
    	MockHttpServletResponse response = result.getResponse();
    	String body = response.getContentAsString();
    	LoginResponse loginResponse = asObject(body, LoginResponse.class);
    	setAccessToken(loginResponse.getAccessToken());
    	setRefreshToken(loginResponse.getRefreshToken());
    }

    public MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
    	assertThat(getAccessToken()).as("The access token is null").isNotNull();
    	return requestBuilder.header("Authorization", "Bearer " + getAccessToken());
    }
    
	public ResultActions getWithAuthTest(String uri, ResultMatcher matcher) throws Exception {
		return withAuth(matcher, HttpMethod.GET, uri);
	}
    
	public ResultActions deleteWithAuthTest(String uri, ResultMatcher matcher) throws Exception {
		return withAuth(matcher, HttpMethod.DELETE, uri);
	}
	
	public ResultActions deleteWithArgsAndAuthTest(ResultMatcher matcher, String uri, Object... uriVariables) throws Exception {
		return withAuth(matcher, HttpMethod.DELETE, uri, uriVariables);
	}
	
	public ResultActions postObjectJsonWithAuthTest(String uri, Object object, ResultMatcher matcher) throws Exception {
		return withAuth(matcher, HttpMethod.POST, uri, object);
	}
	
	private ResultActions withAuth(ResultMatcher matcher, HttpMethod method, String uri) throws Exception {
		ResultActions resultActions = requestWithAuth(MockMvcRequestBuilders.request(method, uri)); 
		if(refreshAuth(resultActions)) {
			resultActions = requestWithAuth(MockMvcRequestBuilders.request(method, uri));
		}		
		return resultActions.andExpect(matcher);
	}
	
	private ResultActions withAuth(ResultMatcher matcher, HttpMethod method, String uri, Object body) throws Exception {
		ResultActions resultActions = requestWithAuth(withJSON(MockMvcRequestBuilders.request(method, uri), body));
		if(refreshAuth(resultActions)) {
			resultActions = requestWithAuth(withJSON(MockMvcRequestBuilders.request(method, uri), body));
		}		
		return resultActions.andExpect(matcher);
	}
	
	private ResultActions withAuth(ResultMatcher matcher, HttpMethod method, String uri, Object... uriVariables) throws Exception {
		ResultActions resultActions = requestWithAuth(MockMvcRequestBuilders.request(method, uri, uriVariables));
		if(refreshAuth(resultActions)) {
			resultActions = requestWithAuth(MockMvcRequestBuilders.request(method, uri, uriVariables));
		}		
		return resultActions.andExpect(matcher);
	}
	
	private ResultActions requestWithAuth(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		return mockMvc.perform(withAuth(requestBuilder));
	}
	
	private boolean refreshAuth(ResultActions resultActions) throws Exception {
		if(resultActions.andReturn().getResponse().getStatus() == HttpStatus.UNAUTHORIZED.value()) {
			// access token expired, refresh token
	    	RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
	    	refreshTokenRequest.setRefreshToken(getRefreshToken());
			ResultActions a = mockMvc
	    			.perform(withJSON(MockMvcRequestBuilders
		    			.post("/api/users/refresh")
		    			, refreshTokenRequest))
	    			.andExpect(status().isOk());
	    	MvcResult result = a.andReturn();
	    	MockHttpServletResponse response = result.getResponse();
	    	String body = response.getContentAsString();
	    	LoginResponse loginResponse = asObject(body, LoginResponse.class);
	    	setAccessToken(loginResponse.getAccessToken());
	    	setRefreshToken(loginResponse.getRefreshToken());
	    	return true;
		}
		return false;
	}
}