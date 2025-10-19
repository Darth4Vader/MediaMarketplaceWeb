package backend.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Annotation to restrict access to users who are NOT admins.
 * <p>
 * This custom annotation is used to secure methods in Spring components
 * to ensure they are only accessible to users who do <b>not</b> have the 'ADMIN' role.
 * It uses Spring Security's {@link PreAuthorize} annotation with a negated role check.
 * </p>
 * 
 * <p>
 * Methods annotated with {@code @AuthenticateNonAdmin} will only be accessible to users
 * <b>without</b> the 'ADMIN' role, allowing user-specific or restricted operations to remain secure.
 * </p>
 * 
 * @see PreAuthorize
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("!hasRole('ADMIN')")
public @interface AuthenticateNonAdmin {
}