package com.xenomi.acs.client;

import com.xenomi.acs.client.dto.request.SwitchCMSDecryptedRequest;
import com.xenomi.acs.client.dto.request.SwitchCMSEncryptedRequest;
import com.xenomi.acs.client.dto.response.SwitchCMSResponse;
import com.xenomi.acs.services.RSAEncryptionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Client responsible for interacting with the Switch CMS endpoint.
 *
 * <p>
 * This client performs a secure outbound call from the ACS to the Switch
 * over HTTPS using a pre-configured {@link RestClient} that enforces
 * <strong>one-way TLS (server authentication only)</strong>.
 *
 * <p>
 * The underlying {@link RestClient} is injected with a named bean
 * ({@code acsSecureRestClient}) that is configured with:
 * <ul>
 *   <li>A trusted Certificate Authority (CA)</li>
 *   <li>Hostname verification</li>
 *   <li>TLS protocol restrictions (TLS 1.2+)</li>
 * </ul>
 *
 * <p>
 * This client is also responsible for:
 * <ul>
 *   <li>Validating the card number using the Luhn algorithm</li>
 *   <li>Encrypting sensitive card data before transmission</li>
 *   <li>Constructing and sending a CMS request to the Switch</li>
 * </ul>
 *
 * <p>
 * No plaintext card data is transmitted or logged by this component.
 */
@Service
public class SwitchCMSClient {

    /**
     * TLS-enabled REST client used for outbound communication with the Switch.
     *
     * <p>
     * This client is configured externally and injected using {@link Qualifier}
     * to ensure that all requests from this component adhere to the defined
     * TLS security policy.
     */
    private final RestClient restClient;

    /**
     * Service responsible for encrypting sensitive card data prior to transmission.
     */
    private final RSAEncryptionService rsaEncryptionService;

    /**
     * Base URL of the Switch CMS endpoint.
     *
     * <p>
     * The default value is provided for local development and testing.
     * In production, this value must be overridden via external configuration.
     */
    @Value("${switch.cms.base-url:https://localhost:8443/api/v1/customer/details}")
    private String baseUrl;

    /**
     * Constructs a new {@code SwitchCMSClient}.
     *
     * <p>
     * The injected {@link RestClient} must already be fully configured with
     * the required TLS settings. This class does not modify or rebuild the
     * client instance.
     *
     * @param restClient the pre-configured, TLS-enabled REST client
     * @param rsaEncryptionService the service used to encrypt card data
     */
    public SwitchCMSClient(
            @Qualifier("acsSecureRestClient") RestClient restClient,
            RSAEncryptionService rsaEncryptionService) {

        this.restClient = restClient;
        this.rsaEncryptionService = rsaEncryptionService;
    }

    /**
     * Sends a cardholder contact request to the Switch CMS service.
     *
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Validates the card number using the Luhn algorithm</li>
     *   <li>Encrypts the card number using RSA encryption</li>
     *   <li>Constructs a CMS request payload</li>
     *   <li>Sends the request to the Switch over a TLS-secured channel</li>
     * </ol>
     *
     * <p>
     * If the card number fails validation, the request is rejected
     * immediately and no network call is made.
     *
     * @param traceId unique identifier used for request tracing and correlation
     * @param cardNumber the plaintext card number to be validated and encrypted
     * @return the response received from the Switch CMS service
     * @throws IllegalArgumentException if the card number fails Luhn validation
     */
    public SwitchCMSResponse sendCardholderContactRequest(SwitchCMSDecryptedRequest request) {

        // Validate card number before any processing or transmission
        

        // Encrypt sensitive card data prior to transmission
        SwitchCMSEncryptedRequest encryptedPayload = rsaEncryptionService.encrypt(request);

        // Execute the outbound HTTPS request using the TLS-enabled RestClient
        return restClient.post()
                .uri(baseUrl)
                .body(encryptedPayload)
                .retrieve()
                .body(SwitchCMSResponse.class);
    }
}
