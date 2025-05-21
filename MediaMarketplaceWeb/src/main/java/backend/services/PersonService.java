package backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.PersonDto;
import backend.dtos.references.PersonReference;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.DirectorRepository;
import backend.repositories.PersonRepository;
import backend.utils.UrlUtils;

/**
 * Service class for managing person entities.
 * <p>
 * This class handles business logic related to persons, including adding, removing, and retrieving
 * person records. It ensures that administrative actions are performed correctly and provides
 * methods to convert between Person entities and PersonDto objects.
 * </p>
 * <p>
 * It acts as an intermediary  between the data access layer (repositories) 
 * and the presentation layer (controllers).
 * </p>
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;
    
    public PersonDto getPerson(Long id) throws EntityNotFoundException {
		// Retrieve the person by ID and convert it to a DTO.
		Person person = getPersonByID(id);
		return convertPersonToDto(person);
	}

    /**
     * Adds a new person to the database.
     * <p>
     * This method is restricted to admin users and adds a new Person entity based on the provided
     * PersonDto. If a person with the same ID already exists, an {@link EntityAlreadyExistsException} is thrown.
     * </p>
     * 
     * @param personDto The {@link PersonDto} containing information about the person to be added.
     * @throws EntityAlreadyExistsException if a person with the same ID already exists in the database.
     */
    @AuthenticateAdmin
    @Transactional
    public Long addPerson(PersonDto personDto) throws EntityAlreadyExistsException {
        try {
            // Load the person by media ID.
            Person person = getPersonByMediaID(personDto.getPersonMediaID());
            // If found, the person already exists in the database, so we notify the user.
            throw new EntityAlreadyExistsException("The Person \"" + person.getMediaId() + "\" already exists");
        } catch (EntityNotFoundException e) {
            // Expected exception if the person does not already exist
        }
        // Convert the DTO to a Person entity and save it to the database.
        Person person = getPersonFromDto(personDto);
        Person resultPerson = personRepository.save(person);
        return resultPerson.getId();
    }

    /**
     * Removes a person from the database.
     * <p>
     * This method is restricted to admin users and deletes the Person entity with the specified ID.
     * It also removes all associated actor roles and directed media. If the person is not found,
     * an {@link EntityNotFoundException} is thrown.
     * </p>
     * 
     * @param id The ID of the person to be removed.
     * @throws EntityNotFoundException if the person with the specified ID does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removePerson(Long id) throws EntityNotFoundException {
        // Retrieve the person by ID.
        Person person = getPersonByID(id);
        // First, delete all associated actor roles and directed media (if exists).
        List<Actor> actorRoles = person.getActorRoles();
        if(actorRoles != null)
        	actorRepository.deleteAllInBatch(actorRoles);
        List<Director> directedMedia = person.getDirectedMedia();
        if(directedMedia != null)
        	directorRepository.deleteAllInBatch(person.getDirectedMedia());
        // Then, delete the person from the database.
        personRepository.delete(person);
    }
    
    /**
     * Retrieves a person by their media ID.
     * 
     * @param personMediaId The media ID of the person to retrieve.
     * @return The {@link Person} entity with the specified media ID.
     * @throws EntityNotFoundException if no person with the specified media ID exists.
     */
    public Person getPersonByMediaID(String personMediaId) throws EntityNotFoundException {
        return personRepository.findByMediaId(personMediaId)
                .orElseThrow(() -> new EntityNotFoundException("The Person with media ID: (" + personMediaId + ") does not exist"));
    }

    /**
     * Retrieves a person by their database ID.
     * 
     * @param id The ID of the person to retrieve.
     * @return The {@link Person} entity with the specified ID.
     * @throws EntityNotFoundException if no person with the specified ID exists.
     */
    public Person getPersonByID(Long id) throws EntityNotFoundException {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Person with ID: \"" + id + "\" does not exist"));
    }
    
    public Page<Person> searchPeople(Specification<Person> specification, Pageable pageable) {
		return personRepository.findAll(specification, pageable);
	}

    /**
     * Converts a {@link PersonDto} to a {@link Person} entity.
     * 
     * @param personDto The {@link PersonDto} to convert.
     * @return A {@link Person} entity populated with the details from the {@link PersonDto}.
     */
    public static Person getPersonFromDto(PersonDto personDto) {
        Person person = new Person();
        person.setMediaId(personDto.getPersonMediaID());
        person.setImagePath(personDto.getImagePath());
        person.setName(personDto.getName());
        person.setBirthDate(personDto.getBirthDate());
        return person;
    }
    
    /**
     * Converts a {@link Person} entity to a {@link PersonDto}.
     * 
     * @param person The {@link Person} entity to convert.
     * @return A {@link PersonDto} containing the details of the person.
     */
    public static PersonDto convertPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();
        personDto.setPersonMediaID(person.getMediaId());
        personDto.setImagePath(UrlUtils.getFullImageURL(person.getImagePath()));
        personDto.setName(person.getName());
        personDto.setBirthDate(person.getBirthDate());
        return personDto;
    }
    
    public static PersonReference convertPersonToReference(Person person) {
        PersonReference personReference = new PersonReference();
        personReference.setId(person.getId());
        personReference.setName(person.getName());
        personReference.setImagePath(UrlUtils.getFullImageURL(person.getImagePath()));
        return personReference;
    }
}