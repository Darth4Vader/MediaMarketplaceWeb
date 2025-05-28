package backend.entities;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie_ratings_info")
@EntityListeners(AuditingEntityListener.class)
public class MovieRating {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "total_ratings", nullable = false)
    private Long totalRatings;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating;

	public MovieRating() {
		// TODO Auto-generated constructor stub
	}
	
	public MovieRating(Movie movie, Long totalRatings, Double averageRating) {
		this.movie = movie;
		this.totalRatings = totalRatings;
		this.averageRating = averageRating;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Movie getMovie() {
		return movie;
	}
	
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	public Long getTotalRatings() {
		return totalRatings;
	}
	
	public void setTotalRatings(Long totalRatings) {
		this.totalRatings = totalRatings;
	}
	
	public Double getAverageRating() {
		return averageRating;
	}
	
	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}
}