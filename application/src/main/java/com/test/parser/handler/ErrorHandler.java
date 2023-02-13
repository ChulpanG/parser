package com.test.parser.handler;

import com.test.parser.domain.exception.InvalidUrlException;
import com.test.parser.rest.ParseHtmlByUrlEndpoint;
import com.test.parser.service.exception.ConnectPageUnexpectedException;
import com.test.parser.service.exception.InvalidPageStructureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConnectPageUnexpectedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ParseHtmlByUrlEndpoint.ParsingRestError handleConnectPageUnexpectedException(ConnectPageUnexpectedException ex){
        return new ParseHtmlByUrlEndpoint.ParsingRestError("page_connect", ex.getMessage());
    }

    @ExceptionHandler(InvalidPageStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ParseHtmlByUrlEndpoint.ParsingRestError handleInvalidPageStructureException(InvalidPageStructureException ex){
        return new ParseHtmlByUrlEndpoint.ParsingRestError("page_parsing", ex.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ParseHtmlByUrlEndpoint.ParsingRestError handleInvalidUrlException(InvalidUrlException ex){
        return new ParseHtmlByUrlEndpoint.ParsingRestError("invalid_url", ex.getMessage());
    }

}
