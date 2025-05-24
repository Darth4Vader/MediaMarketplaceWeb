package backend.user.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import backend.AdminSpringTest;
import backend.dtos.PersonDto;
import backend.dtos.admin.PersonAdminDto;

@TestInstance(Lifecycle.PER_CLASS)
@Transactional
public class PeopleTest extends AdminSpringTest {
	
	private Long personId = 824L;
	
	private static PersonAdminDto testPerson;
	
	@BeforeAll
	public void getPersonTest() throws Exception {
		ResultActions a = getPersonTest(personId, status().isOk());
		testPerson = asObject(a.andReturn().getResponse().getContentAsString(), PersonAdminDto.class);
	}
	
	@BeforeEach
	public void personNotNull() throws Exception {
		assertThat(testPerson).as("The test person is null").isNotNull();
	}
	
	@Test
	public void addPersonTests() throws Exception {
		// Create person dto
		PersonAdminDto personDto = cloneDto(testPerson, PersonAdminDto.class);
		personDto.setId(null);
		
		// The person already exists (same media type)
		addPersonTest(personDto, status().isConflict());
		personDto.setPersonMediaID(RandomStringUtils.randomAlphanumeric(10));
		System.out.println("Person media ID: " + personDto.getPersonMediaID());
		
		// Add new Person
		ResultActions a = addPersonTest(personDto, status().isCreated());
		String locationURI = testHeaderLocationUriMatches(a, "/api/main/people/{id:\\d+}");
		
		// Get new Person
		ResultActions b = getTest(locationURI, status().isOk()); 
		PersonDto getPerson = asObject(b.andReturn().getResponse().getContentAsString(), PersonDto.class);
		
		// check if the person is the same after add -> get
		assertDtoEquals(personDto, getPerson);
		
		// Remove New Person (same uri for getting and removing)
		ResultActions c = deleteWithAuthTest(locationURI, status().isOk());
		
		// check removed person can't be removed again
		ResultActions d = deleteWithAuthTest(locationURI, status().isNotFound());
		
		// Check if the person is removed
		ResultActions e = getTest(locationURI, status().isNotFound());
	}
	
	private ResultActions getPersonTest(Long personId, ResultMatcher matcher) throws Exception {
		return getTestWithArgs(matcher, "/api/main/people/{id}", personId);
	}
	
	private ResultActions addPersonTest(PersonDto person, ResultMatcher matcher) throws Exception {
		return postObjectJsonWithAuthTest("/api/main/people/", person, matcher);
	}
}