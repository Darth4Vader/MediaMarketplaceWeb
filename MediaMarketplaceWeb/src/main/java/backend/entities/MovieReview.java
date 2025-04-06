package backend.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a review for a movie in the database.
 * This entity corresponds to the 'movie_reviews' table and includes details about the review's rating,
 * title, content, creation date, and associated movie and user.
 * 
 * <p>
 * The MovieReview class tracks user reviews for movies, including the review's rating, title, content,
 * and the timestamp when the review was created or last modified.
 * </p>
 * 
 */
@Entity
@Table(name = "movie_reviews")
@EntityListeners(AuditingEntityListener.class)
public class MovieReview {

    /**
     * The unique identifier for this movie review.
     * This field is the primary key of the 'movie_reviews' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The rating given in the review, ranging from 0 to 100.
     * This field maps to the 'rating' column in the 'movie_reviews' table and includes a check constraint
     * to ensure the rating is within this range.
     * 
     * @return the rating
     */
    @Column(columnDefinition = "INT CHECK (rating <= 100 and rating >= 0)")
    private Integer rating;
    
    /**
     * The title of the review.
     * This field maps to the 'review_title' column in the 'movie_reviews' table.
     * 
     * @return the review title
     */
    @Column(name = "review_title")
    private String reviewTitle;
    
    /**
     * The content of the review.
     * This field maps to the 'review' column in the 'movie_reviews' table and has a length limit of 1000 characters.
     * 
     * @return the review content
     */
    @Column(length = 1000)
    private String review;
    
    /**
     * The date and time when the review was created or last modified.
     * This field maps to the 'created_date' column in the 'movie_reviews' table and is automatically set by the auditing system.
     * 
     * @return the date and time of creation or modification
     */
    @LastModifiedDate
    @Column(name = "created_date", nullable = false)
    @NotBlank
    private LocalDateTime createdDate;
    
    /**
     * The movie associated with this review.
     * This field represents a many-to-one relationship with the Movie entity.
     * 
     * @return the associated movie
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    /**
     * The user who wrote this review.
     * This field represents a many-to-one relationship with the User entity.
     * 
     * @return the user who wrote the review
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Gets the unique identifier for this movie review.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this movie review.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the rating given in the review.
     * 
     * @return the rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Sets the rating for this review.
     * 
     * @param rating the rating to set, ranging from 0 to 100
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * Gets the title of the review.
     * 
     * @return the review title
     */
    public String getReviewTitle() {
        return reviewTitle;
    }

    /**
     * Sets the title of the review.
     * 
     * @param reviewTitle the review title to set
     */
    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    /**
     * Gets the content of the review.
     * 
     * @return the review content
     */
    public String getReview() {
        return review;
    }

    /**
     * Sets the content of the review.
     * 
     * @param review the review content to set
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Gets the date and time when the review was created or last modified.
     * 
     * @return the date and time of creation or modification
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date and time when the review was created or last modified.
     * 
     * @param createdDate the date and time to set
     * @throws NullPointerException if the created date is {@code null}
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the movie associated with this review.
     * 
     * @return the associated movie
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Sets the movie associated with this review.
     * 
     * @param movie the movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * Gets the user who wrote this review.
     * 
     * @return the user who wrote the review
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who wrote this review.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}