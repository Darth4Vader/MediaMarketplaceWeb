package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.ActorDto;
import backend.dtos.references.ActorReference;
import backend.entities.Actor;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;

/**
 * Service class for managing actors.
 * <p>
 * This class provides methods to manage actors in the context of movies, including retrieving
 * and adding actors to the database. It handles the business logic related to actors and their
 * roles in movies.
 * </p>
 * <p>
 * It acts as an intermediary between the data access layer (repositories) and
 * the presentation layer (controllers), managing the business logic for actor operations.
 * </p>
 */
@Service
public class ActorService {
	
    @Autowired
    private ActorRepository actorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * Retrieves a list of actors for a given movie.
     * <p>
     * This method is accessible to all users (both logged in and not logged in) and provides
     * information about the actors associated with a specific movie.
     * </p>
     * 
     * @param movieId The ID of the movie for which actors are to be retrieved.
     * @return A list of {@link ActorDto} objects representing the actors of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist.
     */
    public List<ActorDto> getActorsOfMovie(Long movieId) throws EntityNotFoundException {
    	//we will get the entities of all the actors of the movie
    	Movie movie = movieService.getMovieByID(movieId);
        List<Actor> actors = movie.getActorsRoles();
        //and then convert them to dtos
        List<ActorDto> actorsDto = new ArrayList<>();
        if (actors != null) {
            for (Actor actor : actors) {
                ActorDto actorDto = new ActorDto();
                actorDto.setMovieId(movieId);
                actorDto.setPerson(PersonService.convertPersonToDto(actor.getPerson()));
                actorDto.setRoleName(actor.getRoleName());
                actorsDto.add(actorDto);
            }
        }
        return actorsDto;
    }
    
    /**
     * Adds a new actor to a specific movie.
     * <p>
     * This method is restricted to admin users and adds an actor to a movie based on the provided
     * {@link ActorReference} details. It performs checks to ensure the actor does not already exist
     * for the movie.
     * </p>
     * 
     * @param actorReference The {@link ActorReference} object containing the details of the actor to be added.
     * @throws EntityNotFoundException if the person or movie specified in the actorReference does not exist.
     * @throws EntityAlreadyExistsException if the person is already an actor of the specified movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void addActorRole(ActorReference actorReference) throws EntityNotFoundException, EntityAlreadyExistsException {
        Person person = personService.getPersonByMediaID(actorReference.getPersonMediaID());
        Movie movie = movieService.getMovieByNameID(actorReference.getMovieMediaId());
        try {
            // Check if the actor already exists in the movie
            getActorByMovie(movie.getId(), person.getId());
            //if so then he can't be added
            throw new EntityAlreadyExistsException("The person \"" + person.getName() + "\" is already an actor in the movie");
        } catch (EntityNotFoundException e) {
            // Actor does not exist, so we can add them
            Actor actor = new Actor();
            actor.setRoleName(actorReference.getRoleName());
            actor.setPerson(person);
            actor.setMovie(movie);
            //we will save the actor
            actor = actorRepository.save(actor);
            List<Actor> actors = movie.getActorsRoles();
            actors.add(actor);
        }
    }
    
    /**
     * Removes an actor from a specific movie.
     * <p>
     * This method is restricted to admin users and removes an actor from a movie based on the provided
     * {@link ActorReference} details.
     * </p>
     * 
     * @param actorReference The {@link ActorReference} object containing the details of the actor to be removed.
     * @throws EntityNotFoundException if the person or movie specified in the actorReference does not exist,
     * or if the person is not an actor in the movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeActor(ActorReference actorReference) throws EntityNotFoundException {
        Person person = personService.getPersonByMediaID(actorReference.getPersonMediaID());
        Movie movie = movieService.getMovieByNameID(actorReference.getMovieMediaId());
        Actor actor = getActorByMovie(movie.getId(), person.getId());
        List<Actor> actors = movie.getActorsRoles();
        // Remove the actor from the movie and the database
        actors.remove(actor);
        //remove from database
        removeActor(actor);
    }
    
    /**
     * Removes all actors from a specific movie.
     * <p>
     * This method is restricted to admin users and removes all actors associated with a specific movie.
     * </p>
     * 
     * @param movieId The ID of the movie from which all actors are to be removed.
     * @throws EntityNotFoundException if the movie specified by the movieId does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeAllActorsFromMovie(Long movieId) throws EntityNotFoundException {
        Movie movie = movieService.getMovieByID(movieId);
        List<Actor> actors = movie.getActorsRoles();
        // Remove all actors from the movie and clear the list
        if (actors != null) {
            for (Actor actor : actors) {
                removeActor(actor);
            }
        }
        //and then we can remove clear the movie actors list.
        actors.clear();
    }
    
    /**
     * Retrieves an actor by a movie ID and a person ID.
     * 
     * @param movieId The ID of the movie.
     * @param personId The ID of the person.
     * @return The {@link Actor} entity.
     * @throws EntityNotFoundException if the person is not an actor in the movie.
     */
    private Actor getActorByMovie(Long movieId, Long personId) throws EntityNotFoundException {
        return actorRepository.findByMovieIdAndPersonId(movieId, personId)
            .orElseThrow(() -> new EntityNotFoundException("The person \"" + personId + "\" is not an actor in the movie"));
    }
    
    /**
     * Helper method to remove an actor.
     * 
     * @param actor The {@link Actor} to be removed.
     */
    private void removeActor(Actor actor) {
        actorRepository.delete(actor);
    }
}