package backend.dtos;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for representing a person.
 */
public class PersonDto {

    /**
     * The unique identifier for the person.
     */
    private Long id;
    
    /**
     * The name of the person.
     * Must not be blank.
     */
    @NotBlank
    private String name;
    
    /**
     * The path to the image associated with the person.
     */
    private String imagePath;
    
    /**
     * The birth date of the person.
     */
    private Date birthDate;

    /**
     * Gets the unique identifier for the person.
     * 
     * @return the unique identifier of the person
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the person.
     * 
     * @param id the unique identifier of the person
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the person.
     * 
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the person.
     * 
     * @param name the name of the person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the path to the image associated with the person.
     * 
     * @return the image path of the person
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the path to the image associated with the person.
     * 
     * @param imagePath the image path of the person
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the birth date of the person.
     * 
     * @return the birth date of the person
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of the person.
     * 
     * @param birthDate the birth date of the person
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}