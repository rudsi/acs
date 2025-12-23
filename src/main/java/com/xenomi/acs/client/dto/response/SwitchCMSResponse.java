package com.xenomi.acs.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xenomi.acs.client.dto.response.enums.ApiRespFlag;
import com.xenomi.acs.client.dto.response.value.EmailId;
import com.xenomi.acs.client.dto.response.value.MobileNumber;


/**
 * DTO representing the response received from the
 * <b>Switch CMS</b> service.
 *
 * <p>
 * This response is returned for both success and failure scenarios
 * with an HTTP status of {@code 200 OK}. The {@code apiRespFlag}
 * field determines whether the request was processed successfully.
 * </p>
 *
 * <h2>Response Scenarios</h2>
 *
 * <h3>Success (Valid Card)</h3>
 * <ul>
 *   <li>{@code apiRespFlag = "SUCCESS"}</li>
 *   <li>{@code mobileNumber} populated</li>
 *   <li>{@code emailId} populated</li>
 * </ul>
 *
 * <h3>Failure (Invalid Card)</h3>
 * <ul>
 *   <li>{@code apiRespFlag = "FAILED"}</li>
 *   <li>{@code mobileNumber} empty</li>
 *   <li>{@code emailId} empty</li>
 * </ul>
 *
 * <p>
 * This record is immutable, thread-safe, and intended to be used
 * purely as a transport object.
 * </p>
 */
public record SwitchCMSResponse(

        /**
         * Trace identifier echoed back from the request.
         * Used for end-to-end request tracking.
         */
        @JsonProperty("traceId")
        String traceId,

        /**
         * API response flag indicating the result of the request.
         * Expected values: {@code SUCCESS}, {@code FAILED}.
         */
        @JsonProperty("apiRespFlag")
        ApiRespFlag apiRespFlag,

        /**
         * Registered mobile number associated with the card.
         * Empty when {@code apiRespFlag = FAILED}.
         */
        @JsonProperty("mobileNumber")
        MobileNumber mobileNumber,

        /**
         * Registered email address associated with the card.
         * Empty when {@code apiRespFlag = FAILED}.
         */
        @JsonProperty("emailId")
        EmailId emailId
) {
}
