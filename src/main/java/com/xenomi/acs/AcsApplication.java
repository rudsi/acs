package com.xenomi.acs;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.xenomi.acs.client.SwitchCMSClient;
import com.xenomi.acs.client.dto.response.SwitchCMSResponse;

@SpringBootApplication
public class AcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcsApplication.class, args);
	}

	@Bean
    CommandLineRunner run(SwitchCMSClient switchCMSClient) {
        return args -> {
            System.out.println("🚀 [ACS] Starting Cardholder Contact Request...");

            // --- 1. Generate a Valid Trace ID ---
            // Requirement: Alphanumeric, Max 15 chars.
            // Strategy: Generate UUID, remove hyphens, take first 15 chars.
            String rawUuid = UUID.randomUUID().toString().replace("-", "");
            String validTraceId = rawUuid.substring(0, 15);

            System.out.println("Generated Trace ID: " + validTraceId);

            // --- 2. Use a Valid Luhn Card Number ---
            // (Test Visa number)
            String validCardNumber = "4111111111111111";

            try {
                // --- 3. Send Request ---
                SwitchCMSResponse response = switchCMSClient.sendCardholderContactRequest(
                        validTraceId,
                        validCardNumber
                );

                System.out.println("✅ [ACS] Response Received from Switch:");
                System.out.println("   Trace ID: " + response.traceId());
                System.out.println("   Status:   " + response.apiRespFlag());
                System.out.println("   Mobile:   " + response.mobileNumber());
                System.out.println("   Email:    " + response.emailId());

            } catch (Exception e) {
                // This will catch validation errors (if enabled) or connection errors
                System.err.println("[ACS] Request Failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
	}

}
