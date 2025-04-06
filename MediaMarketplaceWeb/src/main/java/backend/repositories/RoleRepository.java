package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Role;
import backend.entities.enums.RoleType;

/**
 * Repository interface for managing {@link Role} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Role} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Role}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a custom method to retrieve a {@link Role}
 * entity based on the role type.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a {@link Role} entity by its role type.
     * 
     * @param roleType the type of the role
     * @return an {@link Optional} containing the found {@link Role} entity, or {@link Optional#empty()} if no role is found
     */
    Optional<Role> findByRoleType(RoleType roleType);
}