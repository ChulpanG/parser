package com.test.parser.common;

public class ErrorRestResponse {

    public record BadRequestRestResponse(String type, String title, String error) {

        public BadRequestRestResponse(String error) {
            this("bad_request", "Bad request", error);
        }
    }
}
