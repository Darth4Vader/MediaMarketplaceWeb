package backend.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Annotation to secure methods by requiring the user to have the 'ADMIN' role.
 * <p>
 * This custom annotation is used to restrict access to methods in Spring components
 * to users who have the 'ADMIN' role. It leverages Spring Security's 
 * {@link PreAuthorize} annotation to enforce this role-based authorization.
 * </p>
 * 
 * <p>
 * Methods annotated with {@code @AuthenticateAdmin} will only be accessible to users
 * with the 'ADMIN' role, ensuring that sensitive operations can only be performed by
 * authorized personnel.
 * </p>
 * 
 * @see PreAuthorize
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface AuthenticateAdmin {
}
