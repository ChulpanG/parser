package com.test.parser.rest;

import com.test.parser.common.RestResponses;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParseHtml;
import com.test.parser.service.error.ParseHtmlError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ParseHtmlByUrlEndpoint {

    private final ParseHtml parseHtml;

    public ParseHtmlByUrlEndpoint(ParseHtml parseHtml) {
        this.parseHtml = parseHtml;
    }

    @GetMapping("/html")
    private Mono<ResponseEntity> getParsedHtml(@RequestParam String url) {
        return Mono.just(Url.from(url).mapLeft(e -> new RestResponses.ValidationError("Invalid url")).fold(
                        RestResponses::toInvalidParamsBadRequest,
                        urlValue -> parseHtml.execute(urlValue).fold(
                                this::toRestResponse,
                                RestResponses::get)
                )
        );
    }

    public record ParsingErrorRestError(String type, String title) {
    }

    private ResponseEntity<ParsingErrorRestError> toRestResponse(ParseHtmlError error) {
        if (error instanceof ParseHtmlError.PageParsingError)
            return ResponseEntity
                    .unprocessableEntity()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(new ParsingErrorRestError("Page parsing error", ((ParseHtmlError.PageParsingError) error).message()));
        if (error instanceof ParseHtmlError.ConnectToPageError)
            return ResponseEntity
                    .unprocessableEntity()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(new ParsingErrorRestError("Connect to page error", ((ParseHtmlError.ConnectToPageError) error).message()));
        return null;
    }
}
