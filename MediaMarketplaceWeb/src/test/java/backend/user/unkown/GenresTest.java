package backend.user.unkown;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import backend.UnknownSpringTest;

public class GenresTest extends UnknownSpringTest {
	
	@Test
	public void getGenresTest() throws Exception {
		getTest("/api/main/genres/", status().isOk());
	}

}