package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.DirectorDto;
import backend.dtos.references.DirectorReference;
import backend.entities.Director;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.DirectorRepository;

/**
 * Service class for managing directors.
 * <p>
 * This class provides methods to manage directors in the context of movies,
 * including retrieving and adding directors to the database.
 * </p>
 * <p>
 * It acts as an intermediary between the data access layer (repositories) and
 * the presentation layer (controllers), handling business logic operations related to directors.
 * </p>
 */
@Service
public class DirectorService {
	
    @Autowired
    private DirectorRepository directorRepository;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MovieService movieService;
    
    /**
     * Retrieves a list of director DTO objects for a given movie.
     * <p>
     * This method is accessible to every user (logged or not logged) and provides information about the directors of a specific movie.
     * </p>
     *
     * @param movieId The ID of the movie for which directors are to be retrieved.
     * @return A list of {@link DirectorDto} objects representing the directors of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist.
     */
    public List<DirectorDto> getDirectorsOfMovie(Long movieId) throws EntityNotFoundException {
    	// We will get the entities of all the directors of the movie
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Director> directors = movie.getDirectors();
    	// And then convert them to DTOs
    	List<DirectorDto> directorsDto = new ArrayList<>();
    	if (directors != null) {
            for (Director director : directors) {
                DirectorDto directorDto = new DirectorDto();
                directorDto.setMovieId(movieId);
                directorDto.setPerson(PersonService.convertPersonToDto(director.getPerson()));
                directorsDto.add(directorDto);
            }
        }
    	return directorsDto;
    }
    
    /**
     * Adds a new director to the database.
     * <p>
     * This method is restricted to admin users and adds a director to a specific movie.
     * </p>
     *
     * @param directorReference The {@link DirectorReference} object containing the details of the director to be added.
     * @throws EntityNotFoundException if the person or movie specified in the directorReference does not exist.
     * @throws EntityAlreadyExistsException if the person is already a director of the specified movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void addDirector(DirectorReference directorReference) throws EntityNotFoundException, EntityAlreadyExistsException {
    	Person person = personService.getPersonByMediaID(directorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(directorReference.getMovieMediaId());
    	try {
    		// If the director exists, then he can't be added
    		getDirectorByMovie(movie.getId(), person.getId());
	    	throw new EntityAlreadyExistsException("The person \"" + person.getName() + "\" is already a director in the movie");
    	} catch (EntityNotFoundException e) {
            // Expected exception if director is not found
        }
    	Director director = new Director();
    	director.setPerson(person);
    	director.setMovie(movie);
    	// We will save the director
    	director = directorRepository.save(director);
    	// And now we add him to the movie
    	List<Director> directors = movie.getDirectors();
    	directors.add(director);
    }
    
    /**
     * Removes a director from the database.
     * <p>
     * This method is restricted to admin users and removes a director from a specific movie.
     * </p>
     *
     * @param directorReference The {@link DirectorReference} object containing the details of the director to be removed.
     * @throws EntityNotFoundException if the person or movie specified in the directorReference does not exist,
     * or if the person is not directing the movie.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeDirector(DirectorReference directorReference) throws EntityNotFoundException {
    	Person person = personService.getPersonByMediaID(directorReference.getPersonMediaID());
    	Movie movie = movieService.getMovieByNameID(directorReference.getMovieMediaId());
    	Director director = getDirectorByMovie(movie.getId(), person.getId());
    	List<Director> directors = movie.getDirectors();
    	// First we will remove the director from the movie
    	directors.remove(director);
    	// And then we can remove him from the database
    	removeDirector(director);
    }
    
    /**
     * Removes all directors from a specific movie.
     * <p>
     * This method is restricted to admin users and removes all directors associated with a specific movie.
     * </p>
     *
     * @param movieId The ID of the movie from which all directors are to be removed.
     * @throws EntityNotFoundException if the movie specified by the movieId does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeAllDirectorsFromMovie(Long movieId) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(movieId);
    	List<Director> directors = movie.getDirectors();
		// First we remove all of the directors
    	if (directors != null) {
            for (Director director : directors) {
                removeDirector(director);
            }
        }
    	// And then we can clear the movie directors list.
		directors.clear();
    }
    
    /**
     * Retrieves a director by a movie ID and a person ID.
     * 
     * @param movieId The ID of the movie.
     * @param personId The ID of the person.
     * @return The {@link Director} entity.
     * @throws EntityNotFoundException if the person is not directing the movie.
     */
    private Director getDirectorByMovie(Long movieId, Long personId) throws EntityNotFoundException {
    	return directorRepository.findByMovieIdAndPersonId(movieId, personId)
    			.orElseThrow(() -> new EntityNotFoundException("The person \"" + personId + "\" is not a director in the movie"));
    }
    
    /**
     * Helper method to remove a director.
     * 
     * @param director The director to be removed.
     */
    private void removeDirector(Director director) {
		directorRepository.delete(director);
    }
}