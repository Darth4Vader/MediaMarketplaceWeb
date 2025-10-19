package backend.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a keyword in the database.
 * This entity corresponds to the 'keywords' table and includes fields that map to its columns.
 * 
 * <p>
 * The Keyword class is used to tag or categorize movies with meaningful terms.
 * It maintains a bidirectional many-to-many relationship with the Movie class,
 * allowing for efficient queries and management of keywords associated with movies.
 * </p>
 */
@Entity
@Table(name = "keywords")
public class Keyword {

	/**
	 * The unique identifier for this keyword.
	 * This field is the primary key of the 'keywords' table.
	 * 
	 * @return the unique identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The list of movies associated with this keyword.
	 * This field represents the many-to-many relationship between keywords and movies.
	 * It is fetched lazily to optimize performance.
	 * 
	 * @return the list of movies associated with this keyword
	 */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "keywords")
	private List<Movie> movies;

	/**
	 * The name of the keyword.
	 * This field maps to the 'name' column in the 'keywords' table and must be unique and not blank.
	 * 
	 * @return the name of the keyword
	 */
	@Column(nullable = false, unique = true)
	@NotBlank
	private String name;

	/**
	 * The media ID associated with this keyword from TMDB.
	 * This field maps to the 'media_id' column in the 'keywords' table.
	 * 
	 * @return the media ID
	 */
	@Column(name = "media_id", nullable = false, unique = true)
	private String mediaID;

	/**
	 * Default constructor for the Keyword class.
	 */
	public Keyword() {
	}

	/**
	 * Constructs a Keyword instance with the specified name and media ID.
	 * 
	 * @param name the name of the keyword
	 * @param mediaID the media ID associated with the keyword
	 */
	public Keyword(@NotBlank String name, @NotBlank String mediaID) {
		this.name = name;
		this.mediaID = mediaID;
	}

	/**
	 * Gets the unique identifier for this keyword.
	 * 
	 * @return the unique identifier
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets the name of the keyword.
	 * 
	 * @return the name of the keyword
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the keyword.
	 * 
	 * @param name the name to set
	 */
	public void setKeywordName(String name) {
		this.name = name;
	}

	/**
	 * Gets the media ID associated with this keyword.
	 * 
	 * @return the media ID
	 */
	public String getMediaID() {
		return mediaID;
	}

	/**
	 * Sets the media ID associated with this keyword.
	 * 
	 * @param mediaID the media ID to set
	 */
	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
	}

	/**
	 * Gets the list of movies associated with this keyword.
	 * 
	 * @return the list of movies associated with this keyword
	 */
	public List<Movie> getMovies() {
		return movies;
	}

	/**
	 * Calculates the hash code for this keyword based on its unique identifier.
	 * 
	 * @return the hash code for this keyword
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Compares this keyword with another object for equality.
	 * Two keywords are considered equal if they have the same unique identifier.
	 * 
	 * @param obj the object to compare with
	 * @return {@code true} if this keyword is equal to the specified object, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Keyword other = (Keyword) obj;
		return Objects.equals(id, other.id);
	}
}