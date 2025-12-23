package com.xenomi.acs.client.dto.response.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Value object representing a customer's mobile number received from the CMS
 * response.
 *
 * <p>
 * Contract semantics:
 * <ul>
 *   <li>This field is <b>response-only</b> and owned by the CMS integration.</li>
 *   <li>When CMS lookup is successful, the value contains a 10-digit Indian
 *       mobile number without country code.</li>
 *   <li>When CMS lookup fails or no mobile is registered, the value is an
 *       empty string ("").</li>
 * </ul>
 *
 * <p>
 * Validation rules are intentionally encapsulated in this class to:
 * <ul>
 *   <li>Prevent validation logic leakage into DTOs</li>
 *   <li>Ensure consistent enforcement across all CMS responses</li>
 *   <li>Fail fast on contract violations from upstream systems</li>
 * </ul>
 *
 * <p>
 * This type must not be reused for request payloads or domain models.
 */
public final class MobileNumber {

    /**
     * Underlying mobile number value.
     *
     * <p>
     * Constraints:
     * <ul>
     *   <li>Empty string is allowed (no registered mobile)</li>
     *   <li>When present, must be exactly 10 digits</li>
     *   <li>Must conform to Indian mobile numbering rules (starts with 6–9)</li>
     * </ul>
     */
    @Size(
        max = 10,
        message = "mobileNumber must be exactly 10 digits when present"
    )
    @Pattern(
        regexp = "^$|^[6-9][0-9]{9}$",
        message = "mobileNumber must be empty or a valid 10-digit Indian mobile number"
    )
    private final String value;

    /**
     * Jackson entry point for deserialization.
     *
     * <p>
     * Null values from CMS are normalized to an empty string to maintain
     * a consistent internal representation and simplify downstream handling.
     *
     * @param value mobile number string received from CMS
     */
    @JsonCreator
    public MobileNumber(String value) {
        this.value = value == null ? "" : value;
    }

    /**
     * Jackson serialization output.
     *
     * @return raw mobile number value as defined by CMS contract
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * Indicates whether a mobile number is actually present.
     *
     * @return {@code true} if a valid mobile number is present,
     *         {@code false} if the value is empty
     */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    /**
     * Returns the raw value for logging and debugging purposes.
     *
     * <p>
     * Callers must ensure masking is applied before logging in
     * production environments.
     */
    @Override
    public String toString() {
        return value;
    }
}
