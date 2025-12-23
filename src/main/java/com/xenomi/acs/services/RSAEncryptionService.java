package com.xenomi.acs.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service responsible for encrypting sensitive data using
 * <b>RSA public key cryptography</b>.
 *
 * <p>
 * The service uses <b>RSA-OAEP with SHA-256</b> padding, which is
 * compliant with modern cryptographic and payment security standards.
 * The public key is loaded once from the application classpath
 * (typically {@code resources/keys}) during service initialization.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Load RSA public key from classpath resources</li>
 *   <li>Encrypt plain text using RSA-OAEP (SHA-256)</li>
 *   <li>Return Base64-encoded encrypted output</li>
 * </ul>
 *
 * <p>
 * This service is stateless, thread-safe, and suitable for use in
 * high-throughput, distributed systems handling sensitive data.
 * </p>
 */
@Service
public class RSAEncryptionService {

    /**
     * RSA transformation using OAEP padding with SHA-256.
     */
    private static final String RSA_TRANSFORMATION =
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * RSA public key used for encryption.
     * Loaded once at startup to avoid repeated I/O.
     */
    private final PublicKey publicKey;

    /**
     * Constructs the service and initializes the RSA public key
     * from the application classpath.
     */
    public RSAEncryptionService() {
        this.publicKey = loadPublicKey("keys/switch_public_key.pem");
    }

    /**
     * Encrypts the provided plain text using the configured RSA public key
     * and OAEP SHA-256 padding.
     *
     * @param plainText sensitive data to encrypt
     * @return Base64-encoded encrypted value
     * @throws IllegalStateException if encryption fails
     */
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new IllegalStateException("RSA encryption failed", e);
        }
    }

    /**
     * Loads an RSA public key from a PEM-encoded file located
     * on the application classpath.
     *
     * <p>
     * The PEM file must be in X.509 format and contain only
     * the public key.
     * </p>
     *
     * @param path classpath location of the PEM file
     * @return initialized {@link PublicKey}
     * @throws IllegalStateException if the key cannot be loaded
     */
    private PublicKey loadPublicKey(String path) {
        try {
            byte[] keyBytes = new ClassPathResource(path)
                    .getInputStream()
                    .readAllBytes();

            String pem = new String(keyBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(pem);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }
}
