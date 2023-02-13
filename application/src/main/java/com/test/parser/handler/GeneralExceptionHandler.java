package com.test.parser.handler;

import com.test.parser.domain.exception.InvalidUrlException;
import com.test.parser.rest.PageParseController;
import com.test.parser.service.exception.ConnectPageUnexpectedException;
import com.test.parser.service.exception.InvalidPageStructureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(ConnectPageUnexpectedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public PageParseController.ParsingRestError handleConnectPageUnexpectedException(ConnectPageUnexpectedException ex){
        return new PageParseController.ParsingRestError("page_loading", ex.getMessage());
    }

    @ExceptionHandler(InvalidPageStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public PageParseController.ParsingRestError handleInvalidPageStructureException(InvalidPageStructureException ex){
        return new PageParseController.ParsingRestError("page_parsing", ex.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public PageParseController.ParsingRestError handleInvalidUrlException(InvalidUrlException ex){
        return new PageParseController.ParsingRestError("invalid_url", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public PageParseController.ParsingRestError handleException(Exception ex){
        return new PageParseController.ParsingRestError("unhandled_exception", ex.getMessage());
    }
}
