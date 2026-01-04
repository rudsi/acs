package com.xenomi.acs.client.dto.request;

/**
 * DTO representing the request payload sent to the Switch CMS service.
 * 
 * <p>
 * This record is immutable, thread-safe, and intended to be used
 * purely as a transport object.
 */
public record SwitchCMSDecryptedRequest(

    String cardNumber,
    String name,
    String mobile,
    String email

) {
    
}
