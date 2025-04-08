package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
//@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:test.properties")
@SpringBootTest(classes = {ActivateSpringApplication.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ApplicationConfiguration(classes = {ActivateSpringApplication.class})
@AutoConfigureMockMvc
//@WithMockUser(username = "frodo", password = "bag")
public class BaseSpringTest {

    @Autowired
    protected MockMvc mockMvc;
    
    public MockHttpServletRequestBuilder withJSON(MockHttpServletRequestBuilder requestBuilder, Object object) {
    	return requestBuilder.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(object));
    }
    
	public String testHeaderLocationUriMatches(ResultActions a, String matchURI) throws Exception {
		a.andExpect(header().exists(HttpHeaders.LOCATION));
		HttpServletRequest request = (HttpServletRequest) a.andReturn().getRequest();
		
		// the URL of the request
		String url = request.getRequestURL().toString();
		// the URI of the request (relative to the URL)
		String requestUri = request.getRequestURI();
		
		// main uri (for example http://localhost:8080)
		String mainUri = url.substring(0, url.indexOf(requestUri));
		// Location header uri
		String locationUri = a.andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
		String relativeLocationUrI = locationUri.substring(locationUri.indexOf(mainUri) + mainUri.length());
		
		AntPathMatcher pathMatcher = new AntPathMatcher();
		assertThat(pathMatcher.match(matchURI, relativeLocationUrI))
			.as("The header location does not match the expected URI")
			.isTrue();
		return locationUri;
	}
    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T asObject(String json, Class<T> clazz) {
		try {
			return new ObjectMapper().readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    public static <T> T cloneDto(T obj, Class<T> clazz) {
    	return asObject(asJsonString(obj), clazz);
    }
    
    public static <T> void assertDtoEquals(T firstObj, T secondObj) {
    	String firstObjString = asJsonString(firstObj);
    	String secondObjString = asJsonString(secondObj);
    	assertThat(firstObjString).as("The two dto are not equal").isEqualTo(secondObjString);
    }
    
	public ResultActions deleteTest(String uri, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(MockMvcRequestBuilders
							.delete(uri))
				.andExpect(matcher);
	}
	
	public ResultActions getTest(String uri, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(MockMvcRequestBuilders
							.get(uri))
				.andExpect(matcher);
	}
	
	public ResultActions getTestWithArgs(ResultMatcher matcher, String uri, Object... uriVariables) throws Exception {
		return mockMvc
				.perform(MockMvcRequestBuilders
							.get(uri, uriVariables))
				.andExpect(matcher);
	}
	
	public ResultActions postObjectJsonTest(String uri, Object object, ResultMatcher matcher) throws Exception {
		return mockMvc
				.perform(withJSON(MockMvcRequestBuilders
							.post(uri),
							object))
				.andExpect(matcher);
	}
}