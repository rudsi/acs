package com.xenomi.acs.client.dto.response.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Value object representing a customer's email identifier received from the CMS
 * response.
 *
 * <p>
 * Contract semantics:
 * <ul>
 *   <li>This field is <b>response-only</b> and owned by the CMS integration.</li>
 *   <li>When CMS lookup is successful and an email is registered, the value
 *       contains a valid email address.</li>
 *   <li>When CMS lookup fails or no email is registered, the value is an
 *       empty string ("").</li>
 * </ul>
 *
 * <p>
 * Validation rules are encapsulated within this value object to:
 * <ul>
 *   <li>Centralize email format enforcement</li>
 *   <li>Ensure consistency across all CMS response DTOs</li>
 *   <li>Fail fast on upstream contract violations</li>
 * </ul>
 *
 * <p>
 * This type must not be reused for request payloads or internal domain models.
 */
public final class EmailId {

    /**
     * Underlying email value.
     *
     * <p>
     * Constraints:
     * <ul>
     *   <li>Empty string is allowed (no registered email)</li>
     *   <li>Maximum length of 50 characters</li>
     *   <li>When present, must conform to RFC-compliant email format</li>
     * </ul>
     */
    @Size(
        max = 50,
        message = "emailId must not exceed 50 characters"
    )
    @Pattern(
        regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "emailId must be empty or a valid email address"
    )
    @Email(
        message = "emailId must be RFC-compliant"
    )
    private final String value;

    /**
     * Jackson entry point for deserialization.
     *
     * <p>
     * Null values from CMS are normalized to an empty string to preserve
     * contract semantics and simplify downstream handling.
     *
     * @param value email identifier received from CMS
     */
    @JsonCreator
    public EmailId(String value) {
        this.value = value == null ? "" : value;
    }

    /**
     * Jackson serialization output.
     *
     * @return raw email value as defined by the CMS contract
     */
    @JsonValue
    public String value() {
        return value;
    }

    /**
     * Indicates whether an email address is actually present.
     *
     * @return {@code true} if a non-empty email value is present,
     *         {@code false} if the value is empty
     */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    /**
     * Returns the raw value for debugging and trace-level logging.
     *
     * <p>
     * Callers must ensure appropriate masking or redaction before logging
     * in production environments to comply with data protection standards.
     */
    @Override
    public String toString() {
        return value;
    }
}
