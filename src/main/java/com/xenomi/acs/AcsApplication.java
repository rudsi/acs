package com.xenomi.acs;

import com.xenomi.acs.client.SwitchCMSClient;
import com.xenomi.acs.client.dto.request.SwitchCMSDecryptedRequest;
import com.xenomi.acs.client.dto.response.SwitchCMSResponse;
import com.xenomi.acs.services.RSAEncryptionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcsApplication.class, args);
    }

    @Bean
    CommandLineRunner run(SwitchCMSClient switchCMSClient, RSAEncryptionService encryptionService) {
        return args -> {
            System.out.println("[ACS] Starting Encrypted Communication Test...");

            
            SwitchCMSDecryptedRequest rawRequest = new SwitchCMSDecryptedRequest(
                "4111111111111111", // Valid Visa Test Number
                "John Doe",
                "9876543210",
                "john.doe@xenomi.com"
            );

            System.out.println("Original Data Prepared:");
            System.out.println("Card:" + rawRequest.cardNumber());

            try {
                // --- 2. Encrypt the Data locally ---
                
                
                // --- 3. Send the Encrypted Payload via the Client ---
                // Note: Ensure your SwitchCMSClient now accepts 'SwitchCMSEncryptedRequest'
                SwitchCMSResponse response = switchCMSClient.sendCardholderContactRequest(
                    rawRequest
                );

                System.out.println("Response: " + response);

            } catch (Exception e) {
                System.err.println("[ACS] Request Failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}