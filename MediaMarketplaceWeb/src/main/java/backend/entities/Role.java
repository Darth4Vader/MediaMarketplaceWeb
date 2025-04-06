package backend.entities;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import backend.entities.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a role within the system. Implements {@link GrantedAuthority} to integrate with Spring Security.
 * <p>
 * A {@code Role} entity defines the authority granted to users, typically representing different access levels or permissions.
 * </p>
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The type of role, represented as an enumeration.
     */
    @Enumerated(EnumType.STRING)
    @Column(unique = true, name = "authority")
    private RoleType roleType;

    /**
     * Default constructor for JPA.
     */
    public Role() {
    }

    /**
     * Constructs a {@code Role} with the specified role type.
     * 
     * @param roleType the type of the role
     */
    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    /**
     * Returns the authority granted by this role.
     * <p>
     * This method is part of the {@link GrantedAuthority} interface.
     * </p>
     * 
     * @return the role type as a string
     */
    @Override
    public String getAuthority() {
        return this.roleType.toString();
    }

    /**
     * Sets the authority type of this role.
     * 
     * @param roleType the type of the role to set
     */
    public void setAuthority(RoleType roleType) {
        this.roleType = roleType;
    }

    /**
     * Returns the unique identifier of this role.
     * 
     * @return the role ID
     */
    public Long getRoleId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of this role.
     * 
     * @param id the ID to set
     */
    public void setRoleId(Long id) {
        this.id = id;
    }

    /**
     * Computes a hash code for this role.
     * <p>
     * The hash code is computed based on the role type and ID.
     * </p>
     * 
     * @return the hash code value for this role
     */
    @Override
    public int hashCode() {
        return Objects.hash(roleType, id);
    }

    /**
     * Compares this role to the specified object for equality.
     * <p>
     * Two roles are considered equal if they have the same role type and ID.
     * </p>
     * 
     * @param obj the object to compare this role to
     * @return true if this role is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        return Objects.equals(roleType, other.roleType) && Objects.equals(id, other.id);
    }
}