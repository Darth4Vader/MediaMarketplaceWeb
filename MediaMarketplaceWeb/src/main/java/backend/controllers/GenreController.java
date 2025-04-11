package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.services.GenreService;
import jakarta.validation.Valid;

/**
 * REST controller for managing genres in the system.
 * <p>
 * This controller provides endpoints for retrieving all genres and creating new genres.
 * </p>
 */
@RestController
@RequestMapping("api/main/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    /**
     * Retrieves all genres available in the system.
     * <p>
     * This method fetches a list of all genre names.
     * </p>
     *
     * @return A list of genre names as {@link String}.
     */
    @GetMapping("/")
    public List<String> getAllGenres() {
        return genreService.getAllGenres();
    }

    /**
     * Creates a new genre with the specified name.
     * <p>
     * This endpoint adds a new genre to the system. If there is an issue with adding the genre, such as a database constraint violation,
     * an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param genreName The name of the genre to be created.
     * @return A {@link ResponseEntity} with a success message if the genre is created successfully.
     * @throws EntityAlreadyExistsException If a genre with the specified name already exists.
     * @throws EntityAdditionException If there is a problem adding the genre due to data access issues.
     */
    @PostMapping("/")
    public ResponseEntity<String> createGenre(@Valid @RequestBody String genreName) throws EntityAlreadyExistsException {
        try {
            genreService.createGenre(genreName);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the genre with the name: " + genreName, e);
        }
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}