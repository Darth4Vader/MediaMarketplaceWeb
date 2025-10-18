package backend.dtos.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecommendResponse {

	public static class Recommendation {
		
		@JsonProperty("movie_id")
		private int movieId;
		
		private double score;
		
		// Getters & setters
		public int getMovieId() {
			return movieId;
		}
		public double getScore() {
			return score;
		}
		public void setMovieId(int movieId) {
			this.movieId = movieId;
		}
		public void setScore(double score) {
			this.score = score;
		}
	}
	
	@JsonProperty("user_id")
    private int userId;
    
    private List<Recommendation> recommendations;

	public int getUserId() {
		return userId;
	}

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}
}