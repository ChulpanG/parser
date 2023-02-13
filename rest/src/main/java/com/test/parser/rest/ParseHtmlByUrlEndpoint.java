package com.test.parser.rest;

import com.test.parser.common.RestResponses;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParsePageService;
import com.test.parser.service.error.ParseHtmlError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ParseHtmlByUrlEndpoint {

    private final ParsePageService parsePageService;

    public ParseHtmlByUrlEndpoint(ParsePageService parsePageService) {
        this.parsePageService = parsePageService;
    }

    @GetMapping("/html")
    private Mono<? extends ResponseEntity<?>> getParsedHtml(@RequestParam String url) {
        return Url.from(url)
                .mapLeft(e -> new RestResponses.ValidationError("Invalid url"))
                .fold(
                        RestResponses::toInvalidParamsBadRequest,
                        urlValue -> parsePageService.execute(urlValue)
                                .flatMap(either ->
                                        Mono.just(either.fold(this::toRestResponse,
                                                RestResponses::get))
                                )
                );
    }

    public record ParsingErrorRestError(String type, String message) {
    }

    private ResponseEntity<ParsingErrorRestError> toRestResponse(ParseHtmlError error) {
        return error instanceof ParseHtmlError.InvalidPageStructureError ?
                ResponseEntity
                        .unprocessableEntity()
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(new ParsingErrorRestError("Page parsing error", ((ParseHtmlError.InvalidPageStructureError) error).message()))
                : null;
    }
}
