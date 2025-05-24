package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.ActorDto;
import backend.dtos.admin.ActorAdminReference;
import backend.dtos.references.ActorReference;
import backend.dtos.references.PersonReference;
import backend.dtos.search.PersonFilter;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.ActorService;
import jakarta.validation.Valid;

/**
 * REST controller for managing actors in the system.
 * <p>
 * This controller provides endpoints for retrieving, adding, and removing actors related to movies.
 * </p>
 */
@RestController
@RequestMapping("api/main/actors")
public class ActorController {

    @Autowired
    private ActorService actorService;
    
    @GetMapping("/search")
    public Page<PersonReference> searchActors(PersonFilter personFilter, Pageable pageable) {
    	return actorService.searchActors(personFilter, pageable);
    }

    /**
     * Retrieves a list of actors associated with a specific movie.
     * <p>
     * This method fetches all actors that are linked to the movie with the specified ID.
     * </p>
     * 
     * @param movieId The ID of the movie for which actors are to be retrieved.
     * @return A list of {@link ActorDto} objects representing the actors of the specified movie.
     * @throws EntityNotFoundException If the movie with the given ID is not found.
     */
    @GetMapping()
    public List<ActorDto> getActorsOfMovie(@Validated @RequestParam("movieId") Long movieId) throws EntityNotFoundException {
        return actorService.getActorsOfMovie(movieId);
    }

    /**
     * Adds a new actor to a movie.
     * <p>
     * This endpoint is used to associate an actor with a movie by providing the actor details.
     * If there is an issue with adding the actor, such as a database constraint violation,
     * a {@link EntityAdditionException} will be thrown.
     * </p>
     * 
     * @param actorDto The {@link ActorReference} containing the details of the actor to be added.
     * @return A {@link ResponseEntity} with a success message if the actor is added successfully.
     * @throws EntityNotFoundException If the movie or actor is not found.
     * @throws EntityAlreadyExistsException If the actor already exists in the movie.
     * @throws EntityAdditionException If there is a problem adding the actor due to data access issues.
     */
    @PostMapping("/")
    public ResponseEntity<String> addActor(@Valid @RequestBody ActorAdminReference actorDto) 
            throws EntityNotFoundException, EntityAlreadyExistsException {
        try {
            actorService.addActorRole(actorDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to create the actor", e);
        }
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }

    /**
     * Removes an actor from a movie.
     * <p>
     * This endpoint is used to dissociate an actor from a movie by providing the actor details.
     * If there is an issue with removing the actor, such as a database constraint violation,
     * a {@link EntityRemovalException} will be thrown.
     * </p>
     * 
     * @param actorDto The {@link ActorReference} containing the details of the actor to be removed.
     * @return A {@link ResponseEntity} with a success message if the actor is removed successfully.
     * @throws EntityNotFoundException If the movie or actor is not found.
     * @throws EntityRemovalException If there is a problem removing the actor due to data access issues.
     */
    @DeleteMapping("/")
    public ResponseEntity<String> removeActor(@Valid @RequestBody ActorAdminReference actorDto) 
            throws EntityNotFoundException {
        try {
            actorService.removeActor(actorDto);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove the actor", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }

    /**
     * Removes all actors from a specific movie.
     * <p>
     * This endpoint removes all actor associations from the given movie. If there is an issue with removing
     * the actors, such as a database constraint violation, an {@link EntityRemovalException} will be thrown.
     * </p>
     * 
     * @param movieId The ID of the movie from which all actors should be removed.
     * @return A {@link ResponseEntity} with a success message if all actors are removed successfully.
     * @throws EntityNotFoundException If the movie with the given ID is not found.
     * @throws EntityRemovalException If there is a problem removing all actors due to data access issues.
     */
    @DeleteMapping("/{movieId}/")
    public ResponseEntity<String> removeAllActorsFromMovie(@PathVariable("movieId") Long movieId) 
            throws EntityNotFoundException {
        try {
            actorService.removeAllActorsFromMovie(movieId);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove all the actors", e);
        }
        return new ResponseEntity<>("Removed All Successfully", HttpStatus.OK);
    }
}