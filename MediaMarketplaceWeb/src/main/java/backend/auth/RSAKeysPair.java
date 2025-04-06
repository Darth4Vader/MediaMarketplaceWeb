package backend.auth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Component;

/**
 * A component for generating and managing RSA key pairs.
 * <p>
 * This class provides functionality to generate a pair of RSA keys (public and private) and
 * allows access to these keys for encryption and decryption operations. The RSA keys are used for
 * secure communication and authentication processes.
 * </p>
 */
@Component
public class RSAKeysPair {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    /**
     * Default constructor that initializes the RSA key pair.
     * <p>
     * Upon creation, this constructor generates a new RSA key pair and sets the public and private
     * keys for this instance.
     * </p>
     */
    public RSAKeysPair() {
        KeyPair pair = generateRSAKeys();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }
    
    /**
     * Generates a new RSA key pair with a key size of 2048 bits.
     * <p>
     * This method uses the RSA algorithm to create a new key pair for encryption and decryption
     * purposes. The key size is set to 2048 bits for adequate security.
     * </p>
     * 
     * @return A {@link KeyPair} object containing the generated public and private keys.
     * @throws IllegalStateException If key generation fails.
     */
    private static KeyPair generateRSAKeys() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch(Exception e) {
            throw new IllegalStateException("Error generating RSA keys", e);
        }

        return keyPair;
    }

    /**
     * Retrieves the RSA public key.
     * 
     * @return The RSA public key as an {@link RSAPublicKey}.
     */
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Sets the RSA public key.
     * 
     * @param publicKey The RSA public key to be set.
     */
    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Retrieves the RSA private key.
     * 
     * @return The RSA private key as an {@link RSAPrivateKey}.
     */
    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Sets the RSA private key.
     * 
     * @param privateKey The RSA private key to be set.
     */
    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}