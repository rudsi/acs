package com.xenomi.acs.client.dto.response;

public record SwitchCMSResponse(

        String status,
        String reasonCode,
        String cardNumber,
        String name,
        String mobile,
        String email

        
) {
}
