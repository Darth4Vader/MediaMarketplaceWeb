package backend.entities.ai;

import java.time.LocalDateTime;

import backend.entities.Movie;
import backend.entities.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movie_page_views")
public class MoviePageView {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Many views belong to one movie
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;
	
	// Many views belong to one user (nullable for anonymous)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String sessionId;      // Spring session ID
	
	private String ip;             // client IP
	
	private LocalDateTime viewedAt;

	public Long getId() {
		return id;
	}

	public Movie getMovie() {
		return movie;
	}

	public User getUser() {
		return user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getIp() {
		return ip;
	}

	public LocalDateTime getViewedAt() {
		return viewedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setViewedAt(LocalDateTime viewedAt) {
		this.viewedAt = viewedAt;
	}
}