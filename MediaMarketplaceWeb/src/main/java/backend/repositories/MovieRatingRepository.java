package backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.MovieRating;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {
	
}