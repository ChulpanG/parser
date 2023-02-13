package com.test.parser.rest;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParsePageService;
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
    private Mono<HtmlInfo> getParsedHtml(@RequestParam String url) {
        var urlName = Url.from(url);
        return parsePageService.execute(urlName);
    }

    public record ParsingRestError(String type, String message) {
    }
}
