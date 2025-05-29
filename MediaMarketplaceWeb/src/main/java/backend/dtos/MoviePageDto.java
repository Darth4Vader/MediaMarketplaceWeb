package backend.dtos;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class MoviePageDto {
	
	@JsonUnwrapped
	private MovieDto movie;
	
	private Double averageRating;
	
	private Long totalRatings;

	public MoviePageDto() {
		// TODO Auto-generated constructor stub
	}

	public MovieDto getMovie() {
		return movie;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public Long getTotalRatings() {
		return totalRatings;
	}

	public void setMovie(MovieDto movie) {
		this.movie = movie;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public void setTotalRatings(Long totalRatings) {
		this.totalRatings = totalRatings;
	}
}