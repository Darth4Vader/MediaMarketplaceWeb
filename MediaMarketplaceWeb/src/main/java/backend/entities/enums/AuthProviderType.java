package backend.entities.enums;

/**
 * Enumeration representing the different authentication providers supported by the system.
 * <p>
 * This enum is used to identify how a user authenticated—via local credentials or a third-party OAuth provider.
 * </p>
 */
public enum AuthProviderType {

    /**
     * Local authentication using email and password.
     */
    LOCAL("Local"),

    /**
     * Authentication via Google OAuth.
     */
    GOOGLE("Google");

    private final String providerName;

    /**
     * Constructor for initializing the auth provider with a specific provider name.
     *
     * @param providerName the name of the provider
     */
    private AuthProviderType(String providerName) {
        this.providerName = providerName;
    }

    /**
     * Gets the name of the authentication provider.
     *
     * @return the provider name
     */
    public String getProviderName() {
        return this.providerName;
    }
}