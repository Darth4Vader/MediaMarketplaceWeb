package backend.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.DataUtils;
import backend.dtos.ActorDto;
import backend.dtos.DirectorDto;
import backend.dtos.MovieDto;
import backend.dtos.references.MovieReference;
import backend.dtos.search.SortDto;
import backend.exceptions.EntityNotFoundException;

/**
 * Utility service for performing search operations related to movies, actors, and directors.
 * Provides methods to search and filter movies based on various criteria.
 */
@Service
public class MovieSearchService {
	
	/**
	 * Controller for managing movie data and operations.
	 */
	@Autowired
	private MovieService movieService;

	/**
	 * Controller for managing movie review data and operations.
	 */
	@Autowired
	private MovieReviewService movierReviewService;

	/**
	 * Controller for managing director data and operations.
	 */
	@Autowired
	private DirectorService directorService;

	/**
	 * Controller for managing actor data and operations.
	 */
	@Autowired
	private ActorService actorService;
	
    /**
     * Searches for movies based on the specified sorting and filtering criteria.
     * 
     * <p>It retrieves all movie references and filters them according to the provided {@link SortDto} criteria.</p>
     * 
     * @param sortDto The sorting and filtering criteria.
     * @return A list of {@link MovieReference} that matches the criteria.
     */
	public List<MovieReference> searchMoviesSort(SortDto sortDto) {
		List<MovieReference> movieList = new ArrayList<>();
		if(!sortDto.isSortEmpty()) {
			//we get references in order to decrease the space complexity, because we load a list of all the movies in the database
			//so we load all of their references, and then we will load every on of the movies separately (one by one)
			List<MovieReference> list = movieService.getAllMovies();
	    	if(list != null) 
	    		for(MovieReference media : list) if(media != null) {
	    			try {
		    			MovieDto movie = movieService.getMovie(media.getId());
		        		if(searchMovie(movie, sortDto))
		        			movieList.add(media);
	    			}
	    			catch (EntityNotFoundException e) {
						//if the problem is not found (which shouldn't happen because we just asked for all the movies in the database, then ignore it
					}
	    		}
		}
        return movieList;
	}
	
    /**
     * Checks if a movie matches the specified sorting and filtering criteria.
     * 
     * @param media The {@link MovieDto} to check.
     * @param sortDto The sorting and filtering criteria.
     * @return {@code true} if the movie matches the criteria; {@code false} otherwise.
     */
	private boolean searchMovie(MovieDto media, SortDto sortDto) {
		if(sortDto.isSortEmpty())
			return false;
		if(sortMovie(media, sortDto)) {
			Long movieId = media.getId();
			String name = sortDto.getName();
			if(DataUtils.isBlank(name))
				return true;
    		String movieName = media.getName();
        	int cur = compare(movieName, name);
        	if(cur > 0)
        		return true;
			try {
				List<ActorDto> actors = actorService.getActorsOfMovie(movieId);
	        	if(searchActors(actors, name))
	        		return true;
			} catch (EntityNotFoundException e) {
				//if there is a problem with loading the actors, then ignore them
			}
			try {
				List<DirectorDto> directors = directorService.getDirectorsOfMovie(movieId);
	    		if(searchDirectors(directors, name))
	    			return true;
			} catch (EntityNotFoundException e) {
				//if there is a problem with loading the directors, then ignore them
			}
		}
		return false;
	}
	
    /**
     * Checks if any actor's name or role name matches the search criteria.
     * 
     * @param actors The list of {@link ActorDto} to check.
     * @param name The search criteria.
     * @return {@code true} if any actor matches the criteria; {@code false} otherwise.
     */
	public static boolean searchActors(List<ActorDto> actors, String name) {
		for(ActorDto actor : actors) {
			if(compare(actor.getRoleName(), name) > 1)
				return true;
			if(compare(actor.getPerson().getName(), name) > 1)
				return true;
		}
		return false;
	}
	
    /**
     * Checks if any director's name matches the search criteria.
     * 
     * @param directors The list of {@link DirectorDto} to check.
     * @param name The search criteria.
     * @return {@code true} if any director matches the criteria; {@code false} otherwise.
     */
	public static boolean searchDirectors(List<DirectorDto> directors, String name) {
		for(DirectorDto director : directors) {
			if(compare(director.getPerson().getName(), name) > 1)
				return true;
		}
		return false;
	}
	
    /**
     * Determines if a movie meets the sorting and filtering criteria.
     * 
     * @param movie The {@link MovieDto} to check.
     * @param sortDto The sorting and filtering criteria.
     * @return {@code true} if the movie meets the criteria; {@code false} otherwise.
     */
	private boolean sortMovie(MovieDto movie, SortDto sortDto) {
		boolean check = true;
		check &= checkBetween(movie.getYear(), sortDto.getYearAbove(), sortDto.getYearBelow());
		check &= checkGenres(movie, sortDto);
		return check
				&& checkBetween(movierReviewService.getMovieRatings(movie.getId()), sortDto.getRatingAbove(), sortDto.getRatingBelow());
	}
	
    /**
     * Checks if a movie's genres match the specified genres in the sorting criteria.
     * 
     * @param movie The {@link MovieDto} to check.
     * @param sortDto The sorting criteria containing genres.
     * @return {@code true} if the genres match; {@code false} otherwise.
     */
	private static boolean checkGenres(MovieDto movie, SortDto sortDto) {
		List<String> genres = sortDto.getGenres();
		List<String> movieGenres = movie.getGenres();
		if(movieGenres == null)
			return false;
		if(genres != null) for(String genre : genres) {
			if(movieGenres.stream().noneMatch(g -> DataUtils.equalsIgnoreCase(g, genre)))
				return false;
		}
		return true;
	}
	
    /**
     * Checks if a value is within the specified range.
     * 
     * @param value The value to check.
     * @param above The lower bound of the range (inclusive).
     * @param below The upper bound of the range (inclusive).
     * @return {@code true} if the value is within the range; {@code false} otherwise.
     */
	private static boolean checkBetween(Integer value, Double above, Double below) {
		boolean bol = true;
		if(above != null) {
			if(value != null)
				bol &= above <= value;
			else
				bol = false;
		}
		if(below != null) {
			if(value != null)
				bol &= value <= below;
			else
				bol = false;
		}
		return bol;
	}
	
    /**
     * Normalizes a string by converting it to lowercase and replacing various punctuation characters with spaces.
     * 
     * @param str The string to normalize.
     * @return The normalized string.
     */
	private static String getString(String str) {
		str = str.toLowerCase();
		str = str.replace(',', ' ');
		str = str.replace('/', ' ');
		str = str.replace(':', ' ');
		str = str.replace('-', ' ');
		str = str.replace('.', ' ');
		str = str.replace('!', ' ');
		return str;
	}
	
    /**
     * Compares two strings based on their content similarity.
     * 
     * <p>The comparison is based on the number of matched substrings between the two strings.</p>
     * 
     * @param str1 The first string.
     * @param str2 The second string.
     * @return The similarity score between the two strings.
     */
	private static int compare(String str1, String str2) {
		str1 = getString(str1);
		str2 = getString(str2);
		List<String> list1 = new ArrayList<>(Arrays.asList(str1.split("\\s+")));
		List<String> list2 = new ArrayList<>(Arrays.asList(str2.split("\\s+")));
		int mainCount = 0;
		for(int i = 0;i < list1.size(); i++)
			for(int j = 0;j < list2.size(); j++) {
				String st = getSubStringMatched(list1.get(i), list2.get(j));
				if(st.equals(list2.get(j))) {
					if(list1.get(i).startsWith(st))
						mainCount++;
					mainCount++;
					list2.remove(j);
					break;
				}
			}
		return mainCount;
	}
	
    /**
     * Finds the longest substring that matches between two strings.
     * 
     * @param str1 The first string.
     * @param str2 The second string.
     * @return The longest matching substring.
     */
	private static String getSubStringMatched(String str1, String str2) {
		String st = "";
		char[] c1 = str1.toCharArray();
		char[] c2 = str2.toCharArray();
		for(int m = 0; m < c1.length; m++)
			for(int n = 0; n < c2.length; n++) {
				if(c1[m] == c2[n]) {
					st = st + c2[n];
					c2[n] = ' ';
					break;
				}
			}
		return st;
	}
}