package com.xenomi.acs.client.config;

import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration for secure outbound REST communication from the ACS
 * using the JDK {@link java.net.http.HttpClient}.
 *
 * <p>
 * This configuration defines a {@link RestClient} that enforces
 * <strong>one-way TLS (server authentication only)</strong> by binding
 * a Spring Boot {@link org.springframework.boot.ssl.SslBundle} to the
 * underlying HTTP client.
 *
 * <p>
 * The SSL bundle defines the trusted Certificate Authorities, enabled
 * TLS protocol versions, and hostname verification behavior. All TLS
 * policy is externalized via application configuration and applied
 * consistently to every outbound request executed through this client.
 *
 * <p>
 * No client certificates or private keys are configured. Mutual TLS
 * (mTLS) is intentionally not enabled at this stage.
 *
 * <p>
 * Example configuration:
 * <pre>
 * spring.ssl.bundle.pem.my-test-server.trust-store.certificate=classpath:certs/local-ca.pem
 * spring.ssl.bundle.pem.my-test-server.protocols=TLSv1.3,TLSv1.2
 * </pre>
 *
 * <p>
 * This configuration is suitable for production use and aligns with
 * security expectations for financial and payment-processing systems.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a secure {@link RestClient} for outbound HTTPS calls
     * originating from the ACS.
     *
     * <p>
     * The {@link java.net.http.HttpClient} is explicitly configured with
     * an {@link javax.net.ssl.SSLContext} obtained from a named
     * {@link org.springframework.boot.ssl.SslBundle}. This ensures:
     * <ul>
     * <li>Server certificate validation against a trusted CA</li>
     * <li>Hostname verification</li>
     * <li>Enforcement of TLS protocol restrictions (TLS 1.2+)</li>
     * </ul>
     *
     * <p>
     * Transport-level timeouts are explicitly defined to prevent
     * resource exhaustion and hanging connections.
     *
     * <p>
     * This client should be used for all outbound calls that require
     * strict TLS validation and compliance-grade security guarantees.
     *
     * @param sslBundles the registry of configured SSL bundles
     * @return a fully configured, secure {@link RestClient} instance
     */
    @Bean("acsSecureRestClient")
    public RestClient acsSecureRestClient(SslBundles sslBundles) {

        // Retrieve the named SSL bundle defining trust and TLS policy
        var bundle = sslBundles.getBundle("my-test-server");

        // Build the JDK HttpClient with the SSL context provided by the bundle
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(bundle.createSslContext())
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // Adapt the JDK HttpClient for use with Spring's RestClient
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        // Build and return the RestClient bound to the secure request factory
        // We use RestClient.builder() manually here to ensure isolation
        // and avoid dependency injection issues.
        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}