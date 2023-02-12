package com.test.parser.common;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RestResponses {

    public record ValidationError(String message) {
    }

    public static ResponseEntity<ErrorRestResponse.BadRequestRestResponse> toInvalidParamsBadRequest(ValidationError error) {

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(new ErrorRestResponse.BadRequestRestResponse(error.message));
    }

    public static <T> ResponseEntity<T> get(T body) {
        return ResponseEntity.ok().body(body);
    }
}
