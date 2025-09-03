package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.ActorDto;
import backend.dtos.admin.ActorAdminReference;
import backend.dtos.references.ActorReference;
import backend.dtos.references.PersonReference;
import backend.dtos.search.PersonFilter;
import backend.entities.Actor;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

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
    
    public Page<PersonReference> searchActors(PersonFilter personFilter, Pageable pageable) {
    	System.out.println(personFilter);
    	Specification<Person> specification = createActorSearchSpecification(personFilter);
		Page<Person> actorPage = personService.searchPeople(specification, pageable);
		
        // Then convert them to DTOs.
        Page<PersonReference> actorReferencesPage = actorPage.map(actor -> {
        	return personService.convertPersonToReference(actor);
		});
        return actorReferencesPage;
    }
    
	public Specification<Person> createActorSearchSpecification(PersonFilter params) {
	    Specification<Person> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        List<Predicate> having = new ArrayList<>();
	        
	        // find all people that are actors
	        Subquery<?> subquery = query.subquery(Long.class);
	        Root<Actor> subqueryRoot = subquery.from(Actor.class);
	        subquery.where(cb.equal(subqueryRoot.get("person").get("id"), root.get("id")));
	        predicates.add(cb.exists(subquery));
	        
            /*Join<Person, Actor> actors = root.join("actorRoles", JoinType.LEFT);
            query.groupBy(root.get("id"));
            having.add(cb.greaterThan(cb.count(actors), (long) 0));
            */
	        if(params.getName() != null) {
	            Expression<Integer> differenceName = cb.function("levenshtein_ratio", Integer.class, root.get("name"), cb.literal(params.getName()));
	            // You can compare if the difference is greater than a threshold value, e.g., 3
	            predicates.add(cb.lessThan(differenceName, 70)); // Adjust the threshold as needed
	            
	            // order by closest matching
	            query.orderBy(cb.asc(differenceName), cb.asc(root.get("name")));
	        }
            if(having.size() > 0) {
            	query.having(cb.and(having.toArray(new Predicate[0])));
			}
            	
	        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	    };
	    return spec;
	}
    
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
                actorDto.setPerson(personService.convertPersonToDto(actor.getPerson()));
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
    public void addActorRole(ActorAdminReference actorAdminReference) throws EntityNotFoundException, EntityAlreadyExistsException {
        Person person = personService.getPersonByMediaID(actorAdminReference.getPersonMediaID());
        Movie movie = movieService.getMovieByNameID(actorAdminReference.getMovieMediaId());
        try {
            // Check if the actor already exists in the movie
            getActorByMovie(movie.getId(), person.getId());
            //if so then he can't be added
            throw new EntityAlreadyExistsException("The person \"" + person.getName() + "\" is already an actor in the movie");
        } catch (EntityNotFoundException e) {
            // Actor does not exist, so we can add them
            Actor actor = new Actor();
            actor.setRoleName(actorAdminReference.getRoleName());
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
    public void removeActor(ActorAdminReference actorAdminReference) throws EntityNotFoundException {
        Person person = personService.getPersonByMediaID(actorAdminReference.getPersonMediaID());
        Movie movie = movieService.getMovieByNameID(actorAdminReference.getMovieMediaId());
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