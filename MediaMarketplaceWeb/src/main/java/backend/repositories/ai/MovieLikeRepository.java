package backend.repositories.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;
import backend.entities.User;
import backend.entities.ai.MovieLike;

@Repository
public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
	
	boolean existsByMovieAndUser(Movie movie, User user);
	
	void deleteByMovieAndUser(Movie movie, User user);
	
	long countByMovie(Movie movie);
}