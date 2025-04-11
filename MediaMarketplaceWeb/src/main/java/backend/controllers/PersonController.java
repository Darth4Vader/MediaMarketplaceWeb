package backend.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import backend.dtos.PersonDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.PersonService;
import jakarta.validation.Valid;

/**
 * REST controller for managing people.
 * <p>
 * This controller provides endpoints for adding and removing people in the system.
 * </p>
 */
@RestController
@RequestMapping("api/main/people")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Adds a new person to the system.
     * <p>
     * This endpoint creates a new person using the provided {@link PersonDto}.
     * If a person with the same media ID already exists, an {@link EntityAlreadyExistsException} will be thrown.
     * If an issue occurs during the transaction, a {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param personDto The {@link PersonDto} object containing the details of the person to be added.
     * @return A {@link ResponseEntity} with a success message and HTTP status 200 (OK).
     * @throws EntityAlreadyExistsException If a person with the same media ID already exists.
     */
    @PostMapping("/")
    public ResponseEntity<?> addPerson(@Valid @RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
        try {
        	// create the person and get the ID
        	final Long id = personService.addPerson(personDto);
        	// build the URI for the created person location
        	URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();
        	return ResponseEntity.created(location).build();
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the person with the media id: \"" + personDto.getPersonMediaID() + "\"", e);
        }
    }
    
    @GetMapping("/{id}")
    public PersonDto getPerson(@PathVariable("id") Long id) throws EntityNotFoundException {
		return personService.getPerson(id);
	}

    /**
     * Removes a person from the system.
     * <p>
     * This endpoint removes a person using the provided ID.
     * If the person cannot be found, an {@link EntityNotFoundException} will be thrown.
     * If an issue occurs during the transaction, a {@link EntityRemovalException} will be thrown.
     * </p>
     *
     * @param id The ID of the person to be removed.
     * @return A {@link ResponseEntity} with a success message and HTTP status 200 (OK).
     * @throws EntityNotFoundException If the person with the provided ID cannot be found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removePerson(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            personService.removePerson(id);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove the person with the id: \"" + id + "\"", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }
}