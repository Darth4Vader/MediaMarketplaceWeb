package backend.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a person in the system, including actors and directors.
 * This entity maps to the 'people' table and stores information about
 * individuals involved in movies, such as actors and directors.
 * 
 * <p>
 * The Person class contains personal details such as name, IMDb ID, image path, and birth date.
 * It also includes lists of roles as actors and directors.
 * </p>
 * 
 */
@Entity
@Table(name = "people")
public class Person {

    /**
     * The unique identifier for this person.
     * This field is the primary key of the 'people' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The IMDb identifier for this person.
     * This field is unique and maps to the 'person_imdb_id' column in the 'people' table.
     * 
     * @return the IMDb identifier
     */
    @Column(name = "media_id", unique = true)
    private String mediaId;
    
    /**
     * The name of this person.
     * This field is required and maps to the 'name' column in the 'people' table.
     * 
     * @return the name of the person
     */
    @Column(nullable = false)
    @NotBlank
    private String name;
    
    /**
     * The path to the image of this person.
     * This field maps to the 'image_path' column in the 'people' table.
     * 
     * @return the image path
     */
    @Column(name = "image_path")
    private String imagePath;
    
    /**
     * The birth date of this person.
     * This field maps to the 'birth_date' column in the 'people' table.
     * 
     * @return the birth date
     */
    @Column(name = "birth_date")
    private Date birthDate;
    
    /**
     * The list of actor roles associated with this person.
     * This field represents a one-to-many relationship with the Actor entity.
     * 
     * @return the list of actor roles
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
    private List<Actor> actorRoles;
    
    /**
     * The list of media directed by this person.
     * This field represents a one-to-many relationship with the Director entity.
     * 
     * @return the list of directed media
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
    private List<Director> directedMedia;

    /**
     * Default constructor for the Person entity.
     */
    public Person() {}

    /**
     * Constructs a Person instance with the specified ID, name, and actor roles.
     * 
     * @param id the unique identifier of the person
     * @param name the name of the person
     * @param actorRoles the list of actor roles associated with this person
     */
    public Person(Long id, @NotBlank String name, List<Actor> actorRoles) {
        this.id = id;
        this.name = name;
        this.actorRoles = actorRoles;
    }

    /**
     * Gets the unique identifier for this person.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this person.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of this person.
     * 
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this person.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Media identifier for this person.
     * 
     * @return the Media identifier
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * Sets the Media identifier for this person.
     * 
     * @param mediaId the Media identifier to set
     */
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    /**
     * Gets the path to the image of this person.
     * 
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the path to the image of this person.
     * 
     * @param imagePath the image path to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the birth date of this person.
     * 
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of this person.
     * 
     * @param birthDate the birth date to set
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the list of actor roles associated with this person.
     * 
     * @return the list of actor roles
     */
    public List<Actor> getActorRoles() {
        return actorRoles;
    }

    /**
     * Sets the list of actor roles associated with this person.
     * 
     * @param actorRoles the list of actor roles to set
     */
    public void setActorRoles(List<Actor> actorRoles) {
        this.actorRoles = actorRoles;
    }

    /**
     * Gets the list of media directed by this person.
     * 
     * @return the list of directed media
     */
    public List<Director> getDirectedMedia() {
        return directedMedia;
    }

    /**
     * Sets the list of media directed by this person.
     * 
     * @param directedMedia the list of directed media to set
     */
    public void setDirectedMedia(List<Director> directedMedia) {
        this.directedMedia = directedMedia;
    }
}