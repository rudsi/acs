package com.xenomi.acs.client.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SwitchCMSRequest(

    @NotBlank
    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[A-Za-z0-9]{1,15}$",
    message = "traceId must be alphanumeric and 1–15 characters")
    @JsonProperty("traceId")
    String traceId,

    @JsonProperty("payload")
    String payload

) {
    
}
