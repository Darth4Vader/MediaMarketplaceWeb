package backend;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

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
}