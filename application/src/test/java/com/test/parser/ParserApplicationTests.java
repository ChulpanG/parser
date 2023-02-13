package com.test.parser;

import com.test.parser.common.ErrorRestResponse;
import com.test.parser.domain.HtmlInfo;
import com.test.parser.rest.ParseHtmlByUrlEndpoint;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParserApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    public static MockWebServer mockServer;

    @BeforeEach
    public void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void successParseHtml() throws Exception {
        prepareMock("correctResponse.html");

        HtmlInfo response = webTestClient.get()
                .uri("/html?url=http://localhost:" + mockServer.getPort())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(HtmlInfo.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertEquals(1, mockServer.getRequestCount());
        Assertions.assertEquals("KEH", response.getName());
        Assertions.assertEquals(70104, response.getReviewsCount());
        Assertions.assertEquals(4.8, response.getRating());
        Assertions.assertEquals("www.keh.com", response.getUrl());
    }

    @Test
    void parseHtmlException() throws Exception {
        prepareMock("invalidResponse.html");

        ParseHtmlByUrlEndpoint.ParsingErrorRestError response = webTestClient.get()
                .uri("/html?url=http://localhost:" + mockServer.getPort())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .returnResult(ParseHtmlByUrlEndpoint.ParsingErrorRestError.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertEquals(1, mockServer.getRequestCount());
        Assertions.assertEquals("Cannot parse null string", response.message());
        Assertions.assertEquals("Page parsing error", response.type());
    }

    @Test
    void responseWithoutField() throws Exception {
        prepareMock("responseWithoutNameField.html");


        ParseHtmlByUrlEndpoint.ParsingErrorRestError response = webTestClient.get()
                .uri("/html?url=http://localhost:" + mockServer.getPort())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .returnResult(ParseHtmlByUrlEndpoint.ParsingErrorRestError.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertEquals(1, mockServer.getRequestCount());
        Assertions.assertEquals("name is marked non-null but is null", response.message());
        Assertions.assertEquals("Page parsing error", response.type());
    }

    @Test
    void badRequest() {
        ErrorRestResponse.BadRequestRestResponse response = webTestClient.get()
                .uri("/html?url=badUrl")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .returnResult(ErrorRestResponse.BadRequestRestResponse.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertEquals(0, mockServer.getRequestCount());
        Assertions.assertEquals(response.type(), "bad_request");
        Assertions.assertEquals(response.title(), "Bad request");
        Assertions.assertEquals(response.error(), "Invalid url");
    }

    private void prepareMock(String responseFileName) throws IOException {
        URL resource = ParserApplicationTests.class.getClassLoader().getResource(responseFileName);
        String htmlPage = FileUtils.readFileToString(new File(resource.getFile()), StandardCharsets.UTF_8);
        mockServer.enqueue(new MockResponse()
                .setBody(htmlPage)
                .addHeader("Content-Type", MediaType.TEXT_HTML_VALUE));
    }
}
