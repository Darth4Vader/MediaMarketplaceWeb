package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Movie;
import backend.entities.MovieRating;
import backend.repositories.MovieRatingRepository;
import backend.repositories.MovieReviewRepository;

@Service
public class MovieRatingService {
	
	@Autowired
	private MovieRatingRepository movieRatingRepository;
	
	@Autowired
	private MovieReviewRepository movieReviewRepository;
	
	@Autowired
	private MovieService movieService;
	
	public void updateAllMoviesRatings() {
		PageRequest pageRequest = PageRequest.of(0, 100);
		Page<Movie> movies = movieService.searchMoviesResult(null, pageRequest);
		while(movies.hasContent()) {
			for(Movie movie : movies.getContent()) {
				updateMovieRating(movie);
			}
			pageRequest = pageRequest.next();
			movies = movieService.searchMoviesResult(null, pageRequest);
		}
	}
	
	@Transactional
	private void updateMovieRating(Movie movie) {
		MovieRating movieRating = movie.getMovieRating();
		System.out.println("Updating movie rating for: " + movie.getName() + " (" + movie.getId() + ")");
		System.out.println("Updating movie rating for: " + movieRating);
		Double averageRating = movieReviewRepository.findAverageRatingByMovie(movie);
		Long totalRatings = movieReviewRepository.findCountByMovie(movie);
		if(movieRating != null) {
			if(totalRatings > 0) {
				movieRating.setAverageRating(averageRating);
				movieRating.setTotalRatings(totalRatings);
				movieRatingRepository.save(movieRating);
			}
			else {
				movie.setMovieRating(null);
				movieService.saveMovie(movie);
				movieRatingRepository.delete(movieRating);
			}
		}
		else {
			if(totalRatings > 0) {
				movieRating = new MovieRating();
				movieRating.setMovie(movie);
				movieRating.setTotalRatings(totalRatings);
				movieRating.setAverageRating(averageRating);
				movie.setMovieRating(movieRating);
				movieService.saveMovie(movie);
				System.out.println(movie.getMovieRating());
			}
		}
		System.out.println("Updating movie rating for: " + movieRating);
	}

}
