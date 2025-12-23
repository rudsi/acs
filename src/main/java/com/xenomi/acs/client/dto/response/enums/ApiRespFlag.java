package com.xenomi.acs.client.dto.response.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApiRespFlag {

    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String value;

    ApiRespFlag(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static ApiRespFlag fromValue(String value) {
        for (ApiRespFlag flag : values()){
            if(flag.value.equalsIgnoreCase(value)) {
                return flag;
            }
        }
        throw new IllegalArgumentException("Unsupported CMS apiRespFlag: " + value);
    }

    
}
