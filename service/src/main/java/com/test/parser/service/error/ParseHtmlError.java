package com.test.parser.service.error;

public sealed interface ParseHtmlError {
    record PageParsingError(String message, String url) implements ParseHtmlError { }
    record ConnectToPageError(String message, String url) implements ParseHtmlError { }
}