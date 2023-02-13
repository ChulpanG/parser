package com.test.parser.service.error;

public sealed interface ParseHtmlError {

    record InvalidPageStructureError(String message, String url) implements ParseHtmlError {
    }
}