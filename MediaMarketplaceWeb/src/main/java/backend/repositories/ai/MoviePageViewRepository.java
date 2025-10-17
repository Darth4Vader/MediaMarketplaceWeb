package backend.repositories.ai;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;
import backend.entities.User;
import backend.entities.ai.MoviePageView;

@Repository
public interface MoviePageViewRepository extends JpaRepository<MoviePageView, Long> {
	// Check if a logged-in user has viewed the movie within cooldown
	boolean existsByMovieAndUserAndViewedAtAfter(Movie movie, User user, LocalDateTime cutoff);

	// Check if an anonymous session has viewed the movie within cooldown
	boolean existsByMovieAndSessionIdAndViewedAtAfter(Movie movie, String sessionId, LocalDateTime cutoff);
	
	Page<MoviePageView> findByViewedAtBefore(LocalDateTime cutoff, Pageable pageable);
}