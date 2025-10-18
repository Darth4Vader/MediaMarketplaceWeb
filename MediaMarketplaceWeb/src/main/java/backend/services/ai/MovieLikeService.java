package backend.services.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.entities.Movie;
import backend.entities.User;
import backend.entities.ai.MovieLike;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ai.MovieLikeRepository;
import backend.services.MovieService;
import backend.services.TokenService;

@Service
public class MovieLikeService {
	
	@Autowired
	private MovieLikeRepository movieLikeRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MovieService movieService;
	
	public void userToggleLikeMovie(Long movieId) throws EntityNotFoundException {
		User user = tokenService.getCurretUser(); // throws if not logged in
		Movie movie = movieService.getMovieByID(movieId);

		boolean alreadyLiked = movieLikeRepository.existsByMovieAndUser(movie, user);
		if (alreadyLiked) {
			movieLikeRepository.deleteByMovieAndUser(movie, user);
		} else {
			MovieLike like = new MovieLike();
			like.setMovie(movie);
			like.setUser(user);
			movieLikeRepository.save(like);
		}
	}
}