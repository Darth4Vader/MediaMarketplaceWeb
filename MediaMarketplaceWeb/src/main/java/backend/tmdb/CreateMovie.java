package backend.tmdb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.ActorController;
import backend.controllers.DirectorController;
import backend.controllers.GenreController;
import backend.controllers.MovieController;
import backend.controllers.PersonController;
import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import backend.dtos.PersonDto;
import backend.dtos.admin.ActorAdminReference;
import backend.dtos.admin.DirectorAdminReference;
import backend.dtos.admin.PersonAdminDto;
import backend.dtos.references.MovieReference;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatusException;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;

/**
 * This class handles operations related to creating and updating movie information
 * using data from The Movie Database (TMDb) API.
 */
@Component
public class CreateMovie {

    /**
     * Base URL for TMDb image resources.
     */
    public static final String TMDB_IMAGE_FULL_URL = "https://www.themoviedb.org/t/p";
    
    /**
     * Base URL for TMDb images.
     */
    public static final String TMDB_IMAGE_URL = "https://image.tmdb.org/t/p/original";
    
    /**
     * TMDb API key for authentication.
     */
    public static final String TMDB_API_KEY = TMDBUtils.loadApiKey();
    
    /**
     * Job title for director in TMDb.
     */
    public static final String DIRECTOR = "director";
    
    /**
     * Path for storing poster images.
     */
    public static final String POSTERS_PATH = "posters/";
    
    /**
     * Path for storing backdrop images.
     */
    public static final String BACKDROP_PATH = "backdrops/";
    
    /**
     * Path for storing people images.
     */
    public static final String PEOPLE_PATH = "people/";
    
    /**
     * Random number generator for introducing delays.
     */
    private static Random rnd = new Random();
    
    @Autowired
    private GenreController genreController;
    
    @Autowired
    private MovieController movieController;
    
    @Autowired
    private PersonController personController;
    
    @Autowired
    private ActorController actorController;
    
    @Autowired
    private DirectorController directorController;
    
    /**
     * Searches for movies using the specified search text.
     *
     * @param text the text to search for
     * @return a {@link MovieDtoSearchResult} containing search results
     */
    public MovieDtoSearchResult searchMovie(String text) {
        return searchMovie(text, null);
    }
    
    /**
     * Searches for movies based on the provided search text and optional pagination.
     *
     * <p>This method utilizes the TMDb API to search for movies matching the specified text. 
     * It supports pagination through the {@code page} parameter. The method converts the 
     * results from the TMDb API into {@link CreateMovieDto} objects to be used within the 
     * system, abstracting away the direct dependency on the TMDb API.</p>
     *
     * @param text the search query for finding movies (e.g., title, keyword)
     * @param page the page number for pagination; if {@code null}, defaults to the first page
     * @return a {@link MovieDtoSearchResult} containing the search results, including:
     *         <ul>
     *           <li>the search text used for querying</li>
     *           <li>the list of movies found, encapsulated in {@link CreateMovieDto} objects</li>
     *           <li>the current page number of the results</li>
     *           <li>the total number of pages available</li>
     *         </ul>
     */
    public MovieDtoSearchResult searchMovie(String text, Integer page) {
    	TmdbApi tmdbApi;
    	try {
        	tmdbApi = new TmdbApi(TMDB_API_KEY);
        }
        catch (ResponseStatusException e) {
        	// if the API key is invalid, throw a specific exception
			if(e.getResponseStatus().getStatusCode() == 7)
				throw new TMDBKeyLoadingException("The API key is invalid");
			else
				throw e;
		}
        TmdbSearch tmdbSearch = tmdbApi.getSearch();
        //we search for all of the results in the given page
        MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(text, null, "en-US", false, page);
        //then we convert the resulting MovieDb to CreateMovieDto, in order to be used in our system without relying on the tmdb api. 
        List<MovieDb> movieDbs = movieResultsPage.getResults();
        List<CreateMovieDto> createMovieDtos = new ArrayList<>();
        if (movieDbs != null) {
            for (MovieDb movieDb : movieDbs) {
                CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
                MovieDto movieDto = createMovieDto.getMovieDto();
                movieDto.setPosterPath(TMDB_IMAGE_FULL_URL + "/w92" + movieDb.getPosterPath());
                createMovieDtos.add(createMovieDto);
            }
        }
        //we create the page dto with all the movie results.
        MovieDtoSearchResult movieDtoSearchResult = new MovieDtoSearchResult();
        movieDtoSearchResult.setSearchText(text);
        movieDtoSearchResult.setResultList(createMovieDtos);
        movieDtoSearchResult.setCurrentPage(movieResultsPage.getPage());
        movieDtoSearchResult.setTotalPages(movieResultsPage.getTotalPages());
        return movieDtoSearchResult;
    }
    
    private static final Logger LOGGER = Logger.getLogger(CreateMovie.class.getName());
    
    static {
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);
    }
    
    /**
     * Gets the logger instance for this class.
     *
     * @return the logger instance
     */
    public Logger getLogger() {
        return LOGGER;
    }
    
    /**
     * Adds a movie to the database using its TMDb ID.
     *
     * <p>This method performs several operations to add a new movie:</p>
     * <ol>
     *   <li>Loads the movie data from TMDb using the provided TMDb ID.</li>
     *   <li>Converts the retrieved data into a {@link CreateMovieDto} for internal processing.</li>
     *   <li>Checks if all genres associated with the movie exist in the database and creates any that are missing.</li>
     *   <li>Sends the {@link CreateMovieDto} to the movie controller to add the movie's details to the database.</li>
     *   <li>Adds or updates the movie's poster and backdrop images in the system.</li>
     *   <li>Adds the directors associated with the movie to the database.</li>
     *   <li>Adds the actors associated with the movie to the database.</li>
     * </ol>
     *
     * @param movieId the TMDb ID of the movie to add
     * @throws CreateMovieException if there are issues creating the movie
     * @throws CanUpdateException if the movie already exists and can be updated
     */
    public void addMovieToDatabase(int movieId) throws CreateMovieException, CanUpdateException {
        List<NameAndException> exceptionList = new ArrayList<>();
        //we load the movie from tmdb
        MovieDb movieDb = getMovieDb(movieId);
        //then we create the movie dto from this to send the MovieController
        CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
        MovieDto movieDto = createMovieDto.getMovieDto();
        //first we check that all of the genres are created and exists.
        createGenres(movieDto);
        String movieMediaID = createMovieDto.getMediaID();
        //Now we send the dto to the controller in order to add the movie in the database
        LOGGER.info("Starting to create the movie information");
        try {
            movieController.addMovie(createMovieDto);
        } catch (EntityNotFoundException e) {
            // This exception is expected if a genre does not exist, but we have verified all genres
        } catch (EntityAlreadyExistsException e) {
            //if the movie already exists, then we can notify the user he can update the movie
        	throw new CanUpdateException(createMovieDto, e);
        }
        LOGGER.info("The movie information has been created");
        //we add posters and backdrops for the movie
        addMovieImages(movieDb, movieDto, exceptionList);
        //then we add the directors to the movie
        addDirectors(movieDb.getCrew(), movieMediaID, exceptionList);
        //then we add the actors to the movie
        addActors(movieDb.getCast(), movieMediaID, exceptionList);
        //if there are any exceptions then inform the user.
        if (!exceptionList.isEmpty()) {
            throw new CreateMovieException(exceptionList, "There were exceptions with creating the movie");
        }
        LOGGER.finer("The Movie has been Added with all of the attributes successfully");
    }
    
    /**
     * Adds a movie to the database using a {@link CreateMovieDto} object.
     *
     * @param createMovieDto the {@link CreateMovieDto} object representing the movie
     * @throws NumberFormatException if the media ID cannot be parsed as an integer
     * @throws CreateMovieException if there are issues creating the movie
     * @throws CanUpdateException if the movie already exists and can be updated
     * 
     * @see #addMovieToDatabase(int)
     */
    public void addMovieToDatabase(CreateMovieDto createMovieDto) throws NumberFormatException, CreateMovieException, CanUpdateException {
        String mediaId = createMovieDto.getMediaID();
        addMovieToDatabase(Integer.parseInt(mediaId));
    }
    
    /**
     * Updates a movie in the database using its TMDb ID.
     *
     * <p>This method performs several operations to update movie information:</p>
     * <ol>
     *   <li>Loads the movie data from TMDb using the provided TMDb ID.</li>
     *   <li>Converts the retrieved data into a {@link CreateMovieDto} for internal processing.</li>
     *   <li>Checks if all genres associated with the movie exist in the database and creates any that are missing.</li>
     *   <li>Sends the {@link CreateMovieDto} to the movie controller to update the movie's details in the database.</li>
     *   <li>Adds or updates the movie's poster and backdrop images in the system.</li>
     *   <li>Removes any existing directors from the movie and adds new ones if there have been changes.</li>
     *   <li>Removes any existing actors from the movie and adds new ones if there have been changes.</li>
     * </ol>
     *
     * @param mediaId the TMDb ID of the movie to update
     * @throws CreateMovieException if there are issues updating the movie
     * @throws EntityNotFoundException if the movie is not found in the database
     */
    private void updateMovieInDatabase(int mediaId) throws CreateMovieException, EntityNotFoundException {
        List<NameAndException> exceptionList = new ArrayList<>();
        //we load the movie from tmdb
        MovieDb movieDb = getMovieDb(mediaId);
        //then we create the movie dto from this to send the MovieController
        CreateMovieDto createMovieDto = getCreateMovieDto(movieDb);
        MovieDto movieDto = createMovieDto.getMovieDto();
        //first we check that all of the genres are created and exists.
        createGenres(movieDto);
        String movieMediaID = createMovieDto.getMediaID();
        LOGGER.info("Starting to update the movie information");
        //Now we send the dto to the controller in order to update the movie in the database.
        final Long movieId = movieController.updateMovie(createMovieDto);
        LOGGER.info("The movie information has been updated");
        movieDto.setId(movieId);
        //we add posters and backdrops for the movie
        addMovieImages(movieDb, movieDto, exceptionList);
        //we check if there are changes with the directors, remove them existing directors for the movie, and then add the new directors to the movie.
        List<PersonCrew> crew = movieDb.getCrew();
        if (crew != null) {
            LOGGER.info("Removing all of the movie directors");
            try {
                directorController.removeAllDirectorsFromMovie(movieId);
            } catch (EntityNotFoundException e) {
                // This should not happen as the movie exists
            }
            LOGGER.info("Finished removing");
            addDirectors(crew, movieMediaID, exceptionList);
        }
        //we check if there are changes with the actors, remove them existing actors for the movie, and then add the new actors to the movie.
        List<PersonCast> cast = movieDb.getCast();
        if (cast != null) {
            LOGGER.info("Removing all of the movie actors");
            try {
                actorController.removeAllActorsFromMovie(movieId);
            } catch (EntityNotFoundException e) {
                // This should not happen as the movie exists
            }
            LOGGER.info("Finished removing");
            addActors(cast, movieMediaID, exceptionList);
        }
        //if there are any exceptions then inform the user.
        if (!exceptionList.isEmpty()) {
            throw new CreateMovieException(exceptionList, "There were exceptions with updating the movie");
        }
        LOGGER.finer("The Movie has been Updated with all of the attributes successfully");
    }
    
    /**
     * Updates a movie in the database using a {@link CanUpdateException} object.
     *
     * @param exception the {@link CanUpdateException} object containing the movie information
     * @throws CreateMovieException if there are issues updating the movie
     * @throws EntityNotFoundException if the movie is not found in the database
     * 
     * @see #updateMovieInDatabase(int)
     */
    public void updateMovieInDatabase(CanUpdateException exception) throws CreateMovieException, EntityNotFoundException {
        updateMovieInDatabase(exception.getCreateMovieDto());
    }
    
    /**
     * Updates a movie in the database using a {@link CreateMovieDto} object.
     *
     * @param createMovieDto the {@link CreateMovieDto} object representing the movie
     * @throws NumberFormatException if the media ID cannot be parsed as an integer
     * @throws CreateMovieException if there are issues updating the movie
     * @throws EntityNotFoundException if the movie is not found in the database
     * 
     * @see #updateMovieInDatabase(int)
     */
    private void updateMovieInDatabase(CreateMovieDto createMovieDto) throws NumberFormatException, CreateMovieException, EntityNotFoundException {
        String mediaId = createMovieDto.getMediaID();
        updateMovieInDatabase(Integer.parseInt(mediaId));
    }
    
    /**
     * Updates a movie in the database using a {@link MovieReference} object.
     *
     * @param movieReference the {@link MovieReference} object representing the movie
     * @throws EntityNotFoundException if the movie is not found in the database
     * @throws NumberFormatException if the media ID cannot be parsed as an integer
     * @throws CreateMovieException if there are issues updating the movie
     * 
     * @see #updateMovieInDatabase(int)
     */
    public void updateMovieInDatabase(MovieReference movieReference) throws EntityNotFoundException, NumberFormatException, CreateMovieException {
    	String mediaId = movieController.getMovieMediaID(movieReference.getId());
        updateMovieInDatabase(Integer.parseInt(mediaId));
    }
    
    /**
     * Retrieves a {@link MovieDb} object from TMDb using the movie ID.
     *
     * @param movieId the TMDb ID of the movie
     * @return the {@link MovieDb} object representing the movie
     */
    private MovieDb getMovieDb(int movieId) {
        TmdbApi tmdbApi = new TmdbApi(TMDB_API_KEY);
        TmdbMovies tmdbMovies = tmdbApi.getMovies();
        return tmdbMovies.getMovie(movieId, "en-US", MovieMethod.credits);
    }
    
    /**
     * Creates genres in the database based on the movie's genre list.
     *
     * @param movieDto the {@link MovieDto} object containing movie details
     */
    private void createGenres(MovieDto movieDto) {
        LOGGER.info("Starting to create genres");
        List<String> genres = movieDto.getGenres();
        for (String genre : genres) {
            try {
                genreController.createGenre(genre);
                LOGGER.info(" The Genre \"" + genre + "\" has been created");
            } catch (EntityAlreadyExistsException e) {
                // This is okay if the genre already exists
            }
        }
    }
    
    /**
     * Adds movie images (poster and backdrop) to the database.
     *
     * @param movieDb the {@link MovieDb} object representing the movie
     * @param movieDto the {@link MovieDto} object containing movie details
     * @param exceptionList the list to store any exceptions encountered
     */
    private void addMovieImages(MovieDb movieDb, MovieDto movieDto, List<NameAndException> exceptionList) {
        LOGGER.info("Adding poster to the movie");
        try {
            saveImageFromURL(movieDb.getPosterPath(), movieDto.getPosterPath());
        } catch (IOException e) {
            exceptionList.add(new NameAndException("Poster Creation Failed", e));
        }
        LOGGER.info("Adding backdrop to the movie");
        try {
            saveImageFromURL(movieDb.getBackdropPath(), movieDto.getBackdropPath());
        } catch (IOException e) {
            exceptionList.add(new NameAndException("Backdrop Creation Failed", e));
        }
    }
    
    /**
     * Adds directors to the movie in the database.
     *
     * @param crew the list of {@link PersonCrew} objects representing the crew
     * @param movieMediaID the media ID of the movie
     * @param exceptionList the list to store any exceptions encountered
     */
    private void addDirectors(List<PersonCrew> crew, String movieMediaID, List<NameAndException> exceptionList) {
        if (crew != null) {
            for (PersonCrew personCrew : crew) {
                String job = personCrew.getJob();
                if (DataUtils.equalsIgnoreCase(job, DIRECTOR)) {
                    PersonAdminDto personDto = getPersonAdminDto(personCrew, exceptionList);
                    DirectorAdminReference directorDto = new DirectorAdminReference();
                    directorDto.setMovieMediaId(movieMediaID);
                    directorDto.setPersonMediaID(personDto.getPersonMediaID());
                    try {
                        directorController.addDirector(directorDto);
                        LOGGER.info("The Director \"" + personDto.getName() + "\" has been added");
                    } catch (EntityNotFoundException e) {
                        exceptionList.add(new NameAndException("Director Creation Failed", e));
                    } catch (EntityAlreadyExistsException e) {
                        // This is okay if the director already exists
                    }
                }
            }
        }
    }
    
    /** The max actors we will add from TMDB is the first 15 actors. */
    private static final int MAX_ACTORS = 15;
    
    /**
     * Adds actors to the movie in the database.
     *
     * @param cast the list of {@link PersonCast} objects representing the cast
     * @param movieMediaID the media ID of the movie
     * @param exceptionList the list to store any exceptions encountered
     */
    private void addActors(List<PersonCast> cast, String movieMediaID, List<NameAndException> exceptionList) {
        int count = 0;
        if (cast != null) {
            for (PersonCast personCast : cast) {
                PersonAdminDto personDto = getPersonAdminDto(personCast, exceptionList);
                ActorAdminReference actorDto = new ActorAdminReference();
                actorDto.setRoleName(personCast.getCharacter());
                actorDto.setMovieMediaId(movieMediaID);
                actorDto.setPersonMediaID(personDto.getPersonMediaID());
                try {
                    actorController.addActor(actorDto);
                    LOGGER.info("The Actor \"" + personDto.getName() + "\" in the role \"" + actorDto.getRoleName() + "\" has been added");
                } catch (EntityNotFoundException e) {
                    exceptionList.add(new NameAndException("Actor Creation Failed", e));
                } catch (EntityAlreadyExistsException e) {
                    // This is okay if the actor already exists
                }
                count++;
                if (count >= MAX_ACTORS) {
                    break;
                }
            }
        }
    }
    
    /**
     * Converts a {@link MovieDb} object into a {@link CreateMovieDto} object.
     *
     * @param movieDb the {@link MovieDb} object representing the movie
     * @return the {@link CreateMovieDto} object
     */
    private CreateMovieDto getCreateMovieDto(MovieDb movieDb) {
        CreateMovieDto createMovieDto = new CreateMovieDto();
        MovieDto movieDto = getMovieDto(movieDb);
        String mediaId = "" + movieDb.getId();
        createMovieDto.setMediaID(mediaId);
        String posterPath = movieDb.getPosterPath();
        if (posterPath != null) {
            movieDto.setPosterPath(POSTERS_PATH + mediaId + ".jpg");
        }
        String backdropPath = movieDb.getBackdropPath();
        if (backdropPath != null) {
            movieDto.setBackdropPath(BACKDROP_PATH + mediaId + ".jpg");
        }
        createMovieDto.setMovieDto(movieDto);
        return createMovieDto;
    }
    
    /**
     * Converts a {@link MovieDb} object into a {@link MovieDto} object.
     *
     * @param movieDb the {@link MovieDb} object representing the movie
     * @return the {@link MovieDto} object
     */
    private MovieDto getMovieDto(MovieDb movieDb) {
        MovieDto movieDto = new MovieDto();
        movieDto.setName(movieDb.getOriginalTitle());
        movieDto.setRuntime(MovieService.convertRuntimeToDto(movieDb.getRuntime()));
        movieDto.setSynopsis(movieDb.getOverview());
        List<Genre> genres = movieDb.getGenres();
        List<String> movieGenres = new ArrayList<>();
        if (genres != null) {
            for (Genre genre : genres) {
                movieGenres.add(genre.getName());
            }
        }
        movieDto.setGenres(movieGenres);
        String releaseDateStr = movieDb.getReleaseDate();
        try {
            LocalDate releaseDate = LocalDate.parse(releaseDateStr);
            movieDto.setReleaseDate(releaseDate);
            movieDto.setYear(releaseDate.getYear());
        } catch (DateTimeParseException e) {
            // Ignore if release date parsing fails
        }
        return movieDto;
    }
    
    /**
     * Converts a {@link Person} object into a {@link PersonDto} object.
     *
     * @param person the {@link Person} object
     * @param exceptionList the list to store any exceptions encountered
     * @return the {@link PersonDto} object
     */
    private PersonAdminDto getPersonAdminDto(Person person, List<NameAndException> exceptionList) {
    	PersonAdminDto personDto = new PersonAdminDto();
        personDto.setName(person.getName());
        personDto.setPersonMediaID("" + person.getId());
        personDto.setImagePath(PEOPLE_PATH + personDto.getPersonMediaID() + ".jpg");
        try {
            try {
                personController.addPerson(personDto);
                LOGGER.info("The Person \"" + personDto.getName() + "\" has been added");
                String personImage = person.getProfilePath();
                if (personImage != null) {
                    LOGGER.info("Saving Person \"" + personDto.getName() + "\" image");
                    saveImageFromURL(personImage, personDto.getImagePath());
                }
            } catch (IOException e) {
                exceptionList.add(new NameAndException("Person Image Creation Failed", e));
            }
        } catch (EntityAlreadyExistsException e) {
            // This is okay if the person already exists
        }
        return personDto;
    }
    
    /**
     * Saves an image from a URL to a specified file path.
     *
     * @param inputURL the URL of the image
     * @param destPath the file path to save the image
     * @return the saved {@link File} object
     * @throws IOException if an I/O error occurs
     */
    public static File saveImageFromURL(String inputURL, String destPath) throws IOException {
        File file = new File(destPath);
        if (!file.exists()) {
            check(PEOPLE_PATH);
            check(POSTERS_PATH);
            check(BACKDROP_PATH);
            URL url;
            try {
                url = URI.create(TMDB_IMAGE_URL + inputURL).toURL();
            } catch (Exception e) {
                throw new IOException("Failed to write");
            }
            BufferedImage image = ImageIO.read(url);
            if (!ImageIO.write(image, "jpg", file)) {
                throw new IOException("Failed to write");
            }
            sleep();
        }
        return file;
    }
    
    /**
     * Pauses the current thread for a random period of time between 1 to 2.5 seconds.
     */
    private static void sleep() {
        int num = rnd.nextInt(1500) + 1000;
        try {
            Thread.sleep(num);
        } catch (InterruptedException exp) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Ensures that the specified directory path exists by creating it if necessary.
     *
     * @param path the directory path
     */
    private static void check(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}