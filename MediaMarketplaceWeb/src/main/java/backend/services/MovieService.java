package backend.services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.CreateMovieDto;
import backend.dtos.MovieDto;
import backend.dtos.MoviePageDto;
import backend.dtos.movies.RuntimeDto;
import backend.dtos.references.MovieReference;
import backend.dtos.search.MovieFilter;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.MovieRating;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;
import backend.sort.entities.MovieSort;
import backend.utils.UrlUtils;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Service class for managing movies.
 * <p>
 * This class handles business logic related to movies within the application, including
 * retrieving movie details, adding new movies, updating existing movies, and handling
 * genre associations. 
 * </p>
 * <p>
 * It acts as an intermediary between the data access layer (repositories)
 * and the presentation layer (controllers), ensuring that all business rules and constraints
 * are enforced.
 * </p>
 */
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private GenreService genreService;
    
    @Autowired
    private UrlUtils urlUtils;

    /**
     * Retrieves a list of all movies.
     * <p>
     * This method fetches all movie records from the database and converts them into
     * {@link MovieReference} objects that represent the movies.
     * </p>
     * 
     * @return A list of {@link MovieReference} objects representing all movies.
     */
    public Page<MovieReference> searchMovies(MovieFilter movieFilter, Pageable pageable) {
		Page<Movie> moviePage = searchMoviesResult(movieFilter, pageable);
		
        // Then convert them to DTOs.
        Page<MovieReference> movieReferencesPage = moviePage.map(movie -> {
        	return convertMovieToReference(movie);
		});
        return movieReferencesPage;
    }
    
    public List<String> getMoviesSearchCategories(MovieFilter movieFilter) {
    	Set<String> genres = new HashSet<>();
        Pageable pageable = PageRequest.of(0, 5000);
        Page<MovieDto> page = getSearchMoviesDto(movieFilter, pageable);
        boolean hasNext = true;
        while(hasNext) {
			pageable = page.nextPageable();
			page = getSearchMoviesDto(movieFilter, pageable);
			hasNext = page.hasNext();
			genres.addAll(page.getContent().stream().map(MovieDto::getGenres).flatMap(List::stream).toList());
        }
        return new ArrayList<>(genres);
    }
    
    private Page<MovieDto> getSearchMoviesDto(MovieFilter movieFilter, Pageable pageable) {
		Page<Movie> moviePage = searchMoviesResult(movieFilter, pageable);
		// Then convert them to DTOs.
		Page<MovieDto> movieDtosPage = moviePage.map(movie -> {
			return convertMovieToDto(movie);
		});
		return movieDtosPage;
	}
    
    public Page<Movie> searchMoviesResult(MovieFilter movieFilter, Pageable pageable) {
    	Sort sort = pageable.getSort();
    	List<Order> customSortOrders = new ArrayList<>();
    	for(Order order : sort) {
    		String property = order.getProperty();
    		MovieSort movieSort = MovieSort.fromValue(property);
    		if(movieSort != null) {
    			customSortOrders.add(order);
    		}
		}
    	if(customSortOrders.size() > 0) {
			// If there are custom sort orders, we apply them.
    		Sort defaultSort = Sort.by(sort.stream()
					    				.filter(order -> !customSortOrders.contains(order))
					    				.toList());
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
		}
    	//PageRequest pageable = PageRequestUtils.getPageRequest(pageableDto);
    	Specification<Movie> specification = createMovieSearchSpecification(movieFilter, Sort.by(customSortOrders));
		Page<Movie> moviePage = movieRepository.findAll(specification, pageable);
		return moviePage;
    }
    
	public Specification<Movie> createMovieSearchSpecification(MovieFilter params, Sort sort) {
	    Specification<Movie> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        List<Predicate> having = new ArrayList<>();
	        List<jakarta.persistence.criteria.Order> orderBy = new ArrayList<>();
	        
	    	if(params == null)
	    		return cb.and(predicates.toArray(new Predicate[predicates.size()]));;
	        
	        if(params.getRatingAbove() != null || params.getRatingBelow() != null) {
	        	Join<Movie, MovieRating> ratings = root.join("movieRating", JoinType.LEFT);
	            if (params.getRatingAbove() != null) {
	                predicates.add(cb.greaterThan(ratings.get("averageRating"), params.getRatingAbove()));
	            }
	            if (params.getRatingBelow() != null) {
	                predicates.add(cb.lessThan(ratings.get("averageRating"), params.getRatingBelow()));
	            }
	        }
	        if(params.getName() != null) {
	            Expression<Integer> differenceName = cb.function("levenshtein_ratio", Integer.class, root.get("name"), cb.literal(params.getName()));
	            // You can compare if the difference is greater than a threshold value, e.g., 3
	            predicates.add(cb.lessThan(differenceName, 70)); // Adjust the threshold as needed
	            
	            // order by closest matching
	            orderBy.add(cb.asc(differenceName));
	            orderBy.add(cb.asc(root.get("name"))); // Ascending order by name
	        }
            if (params.getYearAbove() != null) {
            	LocalDate year = LocalDate.of(params.getYearAbove(), 1, 1);
            	Path<LocalDate> releaseDate = root.get("releaseDate");
            	predicates.add(cb.greaterThanOrEqualTo(releaseDate, year));
            }
            if (params.getYearBelow() != null) {
            	LocalDate year = LocalDate.of(params.getYearBelow(), 12, 31);
            	Path<LocalDate> releaseDate = root.get("releaseDate");
            	predicates.add(cb.lessThanOrEqualTo(releaseDate, year));
            }
            
            if(params.getGenres() != null) {
				Join<Movie, Genre> genres = root.join("genres", JoinType.LEFT);
				List<Long> requestedGenres = params.getGenres();
				
			    // 3) Prevent duplicate root results
			    query.distinct(true);
				
			    Predicate inList = genres.get("id").in(requestedGenres);
			    predicates.add(inList);

			    // 4) Group by movie ID (or full PK if composite)
			    query.groupBy(root.get("id"));

			    // 5) Only keep movies where the count of *distinct* matched names == wanted.size()
			    Expression<Long> countDistinctNames = cb.countDistinct(genres.get("id"));	
			    having.add(cb.equal(countDistinctNames, requestedGenres.size()));
				
			    /*
			    This is the mysql query, if there is a problem, because the code is not true
				    SELECT m.* 
				    FROM movies m
				    JOIN movie_genres mg ON m.id = mg.movie_id
				    JOIN genres g ON mg.genre_id = g.id 
				       AND g.name IN ('Action','Drama','Sci‑Fi','Adventure')
				  GROUP BY m.id
				  HAVING COUNT(DISTINCT g.name) = 4
			    */
			}
            
            if(params.getActors() != null) {
            	List<Long> requestedActors = params.getActors();
            	if(requestedActors.size() > 0) {
            		
    				Join<Movie, Actor> actors = root.join("actorsRoles", JoinType.LEFT);
    				
    			    // 3) Prevent duplicate root results
    			    query.distinct(true);
    				
    			    Predicate inList = actors.get("person").get("id").in(requestedActors);
    			    predicates.add(inList);

    			    // 4) Group by movie ID (or full PK if composite)
    			    query.groupBy(root.get("id"));

    			    // 5) Only keep movies where the count of *distinct* matched num of actors == wanted.size()
    			    Expression<Long> countDistinctActors = cb.countDistinct(actors.get("person").get("id"));	
    			    having.add(cb.equal(countDistinctActors, requestedActors.size()));
    			    
            		
            		/*
            		//this is for OR
        	        Subquery<?> subquery = query.subquery(Long.class);
        	        Root<Actor> subqueryRoot = subquery.from(Actor.class);
        	        subquery.where(
        	        		subqueryRoot.get("person").get("id").in(requestedActorsIds),
        	        		cb.equal(subqueryRoot.get("movie").get("id"), root.get("id"))
        	        );
        	        predicates.add(cb.exists(subquery));
        	        */
				}
            }
            
            if(params.getDirectors() != null) {
            	List<Long> requestedDirectors = params.getDirectors();
            	if(requestedDirectors.size() > 0) {
            		
    				Join<Movie, Director> directors = root.join("directors", JoinType.LEFT);
    				
    			    // 3) Prevent duplicate root results
    			    query.distinct(true);
    				
    			    Predicate inList = directors.get("person").get("id").in(requestedDirectors);
    			    predicates.add(inList);

    			    // 4) Group by movie ID (or full PK if composite)
    			    query.groupBy(root.get("id"));

    			    // 5) Only keep movies where the count of *distinct* matched num of directors == wanted.size()
    			    Expression<Long> countDistinctActors = cb.countDistinct(directors.get("person").get("id"));	
    			    having.add(cb.equal(countDistinctActors, requestedDirectors.size()));
				}
            }
            if(sort != null) {
				for(Order order : sort) {
					String property = order.getProperty();
					MovieSort movieSort = MovieSort.fromValue(property);
					if(movieSort == MovieSort.RATING) {
						Join<Movie, MovieRating> ratings = root.join("movieRating", JoinType.INNER);
						query.groupBy(root.get("id")); // Group by movie ID to ensure correct ordering
						orderBy.add(order.isAscending() ? cb.asc(ratings.get("averageRating")) : cb.desc(ratings.get("averageRating")));
					}
				}
            }
            
            if(having.size() > 0) {
            	query.having(cb.and(having.toArray(new Predicate[0])));
			}
            
            if(orderBy.size() > 0) {
				query.orderBy(orderBy);
            }
            
	        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	    };
	    return spec;
	}

    /**
     * Retrieves details of a specific movie.
     * <p>
     * This method retrieves a {@link Movie} entity by its ID and converts it into a
     * {@link MovieDto} object containing detailed information about the movie.
     * </p>
     * 
     * @param movieId The ID of the movie to retrieve.
     * @return A {@link MovieDto} object containing details of the specified movie.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    public MoviePageDto getMovie(Long movieId) throws EntityNotFoundException {
        Movie movie = getMovieByID(movieId);
        MovieDto movieDto = convertMovieToDto(movie);
        MoviePageDto moviePageDto = new MoviePageDto();
        moviePageDto.setMovie(movieDto);
        MovieRating movieRating = movie.getMovieRating();
        if (movieRating != null) {
        	Long totalRatings = movieRating.getTotalRatings();
        	if(totalRatings != null && totalRatings > 0) {
				// If there are ratings, then we set the average rating and total ratings.
				moviePageDto.setAverageRating((int) Math.round(movieRating.getAverageRating()));
				moviePageDto.setTotalRatings(movieRating.getTotalRatings());
			}
		}
        return moviePageDto;
    }

    /**
     * Adds a new movie to the database.
     * <p>
     * This method adds a new movie using the details provided in the {@link CreateMovieDto}.
     * It is restricted to admin users only. If a movie with the same media ID already exists,
     * an {@link EntityAlreadyExistsException} is thrown. The method also verifies that all genres
     * specified in the movie details exist in the database.
     * </p>
     * 
     * @param createMovieDto The {@link CreateMovieDto} object containing details of the movie to add.
     * @throws EntityAlreadyExistsException if a movie with the same mediaID already exists.
     * @throws EntityNotFoundException if any of the genres specified do not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void addMovie(CreateMovieDto createMovieDto) throws EntityAlreadyExistsException, EntityNotFoundException {
        String mediaID = createMovieDto.getMediaID();
        try {
            // Load the movie from the database
            getMovieByNameID(mediaID);
            // If it is already in the database, then we already added it.
            throw new EntityAlreadyExistsException("The Movie with mediaId: (" + mediaID + ") already exists");
        } catch (EntityNotFoundException e) {
            // This exception is expected if the movie does not already exist.
        }
        MovieDto movieDto = createMovieDto.getMovieDto();
        List<String> genresNames = movieDto.getGenres();
        List<Genre> genres = new ArrayList<>();
        // Find the new genres in the genres database
        for (String genreName : genresNames) {
            genres.add(genreService.getGenreByName(genreName));
        }
        // Create the movie and save it into the database
        Movie movie = new Movie();
        movie.setMediaID(mediaID);
        movie.setGenres(genres);
        updateMovieByDto(movie, movieDto);
        movieRepository.save(movie);
    }

    /**
     * Updates an existing movie in the database.
     * <p>
     * This method updates an existing movie using the details provided in the {@link CreateMovieDto}.
     * It is restricted to admin users only. If the movie does not exist, an {@link EntityNotFoundException}
     * is thrown. The movie's genres are updated if new genres are provided.
     * </p>
     * 
     * @param createMovieDto The {@link CreateMovieDto} object containing updated details of the movie.
     * @return The ID of the updated movie.
     * @throws EntityNotFoundException if the movie with the specified mediaID does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public Long updateMovie(CreateMovieDto createMovieDto) throws EntityNotFoundException {
        // Load the movie
        String mediaID = createMovieDto.getMediaID();
        MovieDto movieDto = createMovieDto.getMovieDto();
        Movie movie = getMovieByNameID(mediaID);
        // Update the movie's genres only if there are new genres provided.
        List<String> genresNames = movieDto.getGenres();
        if (genresNames != null) {
            movie.setGenres(null); // Remove current genres
            List<Genre> newGenres = new ArrayList<>();
            for (String genreName : genresNames) {
                newGenres.add(genreService.getGenreByName(genreName));
            }
            movie.setGenres(newGenres); // Set new genres
        }
        // Update the movie and save it
        updateMovieByDto(movie, movieDto);
        Movie updatedMovie = movieRepository.save(movie);
        return updatedMovie.getId();
    }

    /**
     * Retrieves the media ID of a specific movie.
     * <p>
     * This method is restricted to admin users only.
     * </p>
     * 
     * @param movieId The ID of the movie.
     * @return The media ID of the specified movie.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @AuthenticateAdmin
    public String getMovieMediaID(Long movieId) throws EntityNotFoundException {
        Movie movie = getMovieByID(movieId);
        return movie.getMediaID();
    }
    
    /**
     * Retrieves a {@link Movie} entity by its media ID.
     * 
     * @param mediaID The media ID of the movie.
     * @return The {@link Movie} entity associated with the given media ID.
     * @throws EntityNotFoundException if no movie with the specified media ID exists.
     */
    public Movie getMovieByNameID(String mediaID) throws EntityNotFoundException {
        return movieRepository.findByMediaID(mediaID)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with mediaId: (" + mediaID + ") does not exist"));
    }

    /**
     * Retrieves a {@link Movie} entity by its ID.
     * 
     * @param id The ID of the movie.
     * @return The {@link Movie} entity associated with the given ID.
     * @throws EntityNotFoundException if no movie with the specified ID exists.
     */
    public Movie getMovieByID(Long id) throws EntityNotFoundException {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with ID: (" + id + ") does not exist"));
    }
    
    @Transactional
    public void saveMovie(Movie movie) {
		movieRepository.save(movie);
	}

    /**
     * Converts a {@link Movie} entity to a {@link MovieReference} DTO.
     * 
     * @param movie The {@link Movie} entity to convert.
     * @return A {@link MovieReference} DTO representing the movie.
     */
    public MovieReference convertMovieToReference(Movie movie) {
        MovieReference movieReference = new MovieReference();
        movieReference.setId(movie.getId());
        movieReference.setName(movie.getName());
        movieReference.setPosterPath(urlUtils.getFullImageURL(movie.getPosterPath()));
        return movieReference;
    }

    /**
     * Converts a {@link Movie} entity to a {@link MovieDto}.
     * 
     * @param movie The {@link Movie} entity to convert.
     * @return A {@link MovieDto} object containing detailed information about the movie.
     */
    public MovieDto convertMovieToDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setSynopsis(movie.getSynopsis());
        movieDto.setPosterPath(urlUtils.getFullImageURL(movie.getPosterPath()));
        movieDto.setBackdropPath(urlUtils.getFullImageURL(movie.getBackdropPath()));
        movieDto.setRuntime(convertRuntimeToDto(movie.getRuntime()));
        movieDto.setName(movie.getName());
        List<Genre> genres = movie.getGenres();
        List<String> genresNameList = GenreService.convertGenresToDto(genres);
        movieDto.setGenres(genresNameList);
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setYear(movie.getYear());
        return movieDto;
    }
    
    public static RuntimeDto convertRuntimeToDto(Integer runtime) {
		RuntimeDto runtimeDto = new RuntimeDto();
		runtimeDto.setMinutes(runtime);
		if(runtime != null) {
			Duration runtimeDuration = Duration.ofMinutes(runtime);
			String formattedText = "";
			if(runtimeDuration.toHoursPart() > 0) {
				formattedText += runtimeDuration.toHoursPart() + "h";
			}
			if(runtimeDuration.toMinutesPart() > 0) {
				if(!formattedText.isEmpty()) {
					formattedText += " ";
				}
				formattedText += runtimeDuration.toMinutesPart() + "m";
			}
			runtimeDto.setFormattedText(formattedText);
		}
		return runtimeDto;
	}

    /**
     * Updates the properties of a {@link Movie} entity based on the provided {@link MovieDto}.
     * <p>
     * If a given field in the DTO is null, it will not update that field in the movie entity.
     * </p>
     * 
     * @param movie The {@link Movie} entity to update.
     * @param movieDto The {@link MovieDto} object containing updated details.
     */
    private static void updateMovieByDto(Movie movie, MovieDto movieDto) {
        String synopsis = movieDto.getSynopsis();
        if (synopsis != null) movie.setSynopsis(synopsis);
        String posterPath = movieDto.getPosterPath();
        if (posterPath != null) movie.setPosterPath(posterPath);
        String backdropPath = movieDto.getBackdropPath();
        if (backdropPath != null) movie.setBackdropPath(backdropPath);
        RuntimeDto runtime = movieDto.getRuntime();
        if (runtime != null) movie.setRuntime(runtime.getMinutes());
        String name = movieDto.getName();
        if (name != null) movie.setName(name);
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setYear(movieDto.getYear());
    }
}