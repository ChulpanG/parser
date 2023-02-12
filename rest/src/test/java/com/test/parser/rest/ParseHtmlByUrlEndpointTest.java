package com.test.parser.rest;

import com.test.parser.common.ErrorRestResponse;
import com.test.parser.domain.HtmlInfo;
import com.test.parser.service.ParseHtml;
import com.test.parser.service.error.ParseHtmlError;
import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = ParseHtmlByUrlEndpoint.class)
@ContextConfiguration(classes = ParseHtmlByUrlEndpoint.class)
public class ParseHtmlByUrlEndpointTest {

    private static final String HTML_URL = "https://www.sitejabber.com/reviews/keh.com";

    @MockBean
    ParseHtml parseHtml;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void parsedHtmlSuccessfully() {
        var htmlInfo = HtmlInfo.builder()
                .url("www.keh.com")
                .name("KEH")
                .rating(4.7)
                .reviewsCount(80000)
                .build();
        Mockito.when(parseHtml.execute(Mockito.any())).thenReturn(Either.right(htmlInfo));
        var res = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/html")
                        .queryParam("url", HTML_URL).build())
                .exchange()
                .expectBody(HtmlInfo.class)
                .returnResult();
        Assertions.assertNotNull(res.getResponseBody());
        Assertions.assertEquals(res.getResponseBody().getUrl(), htmlInfo.getUrl());
        Assertions.assertEquals(res.getResponseBody().getRating(), htmlInfo.getRating());
        Assertions.assertEquals(res.getResponseBody().getReviewsCount(), htmlInfo.getReviewsCount());
        Assertions.assertEquals(res.getResponseBody().getName(), htmlInfo.getName());
    }

    @Test
    void parsingHtmlFailing_ConnectToPageError() {
        var errorMessage = "Fail";
        Mockito.when(parseHtml.execute(Mockito.any()))
                .thenReturn(Either.left(new ParseHtmlError.ConnectToPageError(errorMessage, HTML_URL)));
        var res = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/html")
                        .queryParam("url", HTML_URL).build())
                .exchange()
                .expectBody(ParseHtmlByUrlEndpoint.ParsingErrorRestError.class)
                .returnResult();
        Assertions.assertNotNull(res.getResponseBody());
        Assertions.assertEquals(res.getResponseBody().type(), "Connect to page error");
        Assertions.assertEquals(res.getResponseBody().title(), errorMessage);
    }

    @Test
    void parsingHtmlFailing_PageParsingError() {
        var errorMessage = "Fail";
        Mockito.when(parseHtml.execute(Mockito.any()))
                .thenReturn(Either.left(new ParseHtmlError.PageParsingError(errorMessage, HTML_URL)));
        var res = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/html")
                        .queryParam("url", HTML_URL).build())
                .exchange()
                .expectBody(ParseHtmlByUrlEndpoint.ParsingErrorRestError.class)
                .returnResult();
        Assertions.assertNotNull(res.getResponseBody());
        Assertions.assertEquals(res.getResponseBody().type(), "Page parsing error");
        Assertions.assertEquals(res.getResponseBody().title(), errorMessage);
    }

    @Test
    void parsingHtmlFailing_badRequest() {
        var res = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/html")
                        .queryParam("url", "l").build())
                .exchange()
                .expectBody(ErrorRestResponse.BadRequestRestResponse.class)
                .returnResult();
        Assertions.assertNotNull(res.getResponseBody());
        Assertions.assertEquals(res.getResponseBody().type(), "bad_request");
        Assertions.assertEquals(res.getResponseBody().title(), "Bad request");
        Assertions.assertEquals(res.getResponseBody().error(), "Invalid url");
    }
}
