package backend.entities.enums;

/**
 * Enumeration representing the different roles a user can have in the system.
 * <p>
 * Each role defines a set of permissions and access levels within the application.
 * </p>
 */
public enum RoleType {

    /**
     * Role with administrative privileges. Typically has access to all system features.
     * <p>
     * Users with this role can perform administrative tasks such as managing other users,
     * configuring system settings, and accessing all parts of the application.
     * </p>
     */
    ROLE_ADMIN("ADMIN"),

    /**
     * Standard user role with basic access rights.
     * <p>
     * Users with this role have access to standard features and functionalities of the application,
     * but do not have administrative privileges.
     * </p>
     */
    ROLE_USER("USER");
    
    private final String roleName;
    
    /**
     * Constructor for initializing the role type with a specific role name.
     * 
     * @param roleName the name of the role
     */
    private RoleType(String roleName) {
        this.roleName = roleName;
    }
    
    /**
     * Gets the name of the role.
     * 
     * @return the name of the role
     */
    public String getRoleName() {
        return this.roleName;
    }
}