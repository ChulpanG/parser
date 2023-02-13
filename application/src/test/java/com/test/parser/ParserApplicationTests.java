package com.test.parser;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.rest.PageParseController;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParserApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    public static MockWebServer mockServer;

    @BeforeAll
    static void setUp() throws IOException {
        final Dispatcher dispatcher = new Dispatcher() {
            @NotNull
            @SneakyThrows
            @Override
            public MockResponse dispatch(RecordedRequest request) {

                return switch (request.getPath()) {
                    case "/correct" -> prepareMockByFile("correctResponse.html");
                    case "/requiredFieldsMissing" -> prepareMockByFile("responseWithoutNameField.html");
                    case "/incorrectStructure" -> new MockResponse().setBody("<html</html>");
                    case "/internalServerError" -> new MockResponse().setResponseCode(500);
                    default -> new MockResponse().setResponseCode(404);
                };
            }
        };
        mockServer = new MockWebServer();
        mockServer.setDispatcher(dispatcher);
        mockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void successParseHtml() {
        var response = webTestClient.get()
                .uri("/html?url=http://" + mockServer.getHostName() + ":" + mockServer.getPort() + "/correct")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(HtmlInfo.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("KEH", response.getName());
        Assertions.assertEquals(70104, response.getReviewsCount());
        Assertions.assertEquals(4.8, response.getRating());
        Assertions.assertEquals("www.keh.com", response.getUrl());
    }

    @Test
    void parseHtmlException() {
        var response = webTestClient.get()
                .uri("/html?url=http://" + mockServer.getHostName() + ":" + mockServer.getPort() + "/incorrectStructure")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(PageParseController.ParsingRestError.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("java.lang.NullPointerException", response.systemMessage());
        Assertions.assertEquals("page_parsing", response.type());
    }

    @Test
    void responseWithoutField() {
        var response = webTestClient.get()
                .uri("/html?url=http://" + mockServer.getHostName() + ":" + mockServer.getPort() + "/requiredFieldsMissing")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(PageParseController.ParsingRestError.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("java.lang.NullPointerException: name is marked non-null but is null", response.systemMessage());
        Assertions.assertEquals("page_parsing", response.type());
    }

    @Test
    void badRequest() {
        var response = webTestClient.get()
                .uri("/html?url=badUrl")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(PageParseController.ParsingRestError.class)
                .getResponseBody()
                .blockFirst();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.type(), "invalid_url");
        Assertions.assertEquals(response.systemMessage(), "Invalid url");
    }

    @Test
    void code500FromWebPage() {
        var response = webTestClient.get()
                .uri("/html?url=http://" + mockServer.getHostName() + ":" + mockServer.getPort() + "/internalServerError")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(PageParseController.ParsingRestError.class)
                .getResponseBody()
                .blockFirst();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.type(), "page_loading");
        Assertions.assertEquals(response.systemMessage(), "Error while request site");
    }

    private static MockResponse prepareMockByFile(String responseFileName) throws IOException {
        URL resource = ParserApplicationTests.class.getClassLoader().getResource(responseFileName);
        String htmlPage = FileUtils.readFileToString(new File(resource.getFile()), StandardCharsets.UTF_8);
        return new MockResponse()
                .setBody(htmlPage)
                .addHeader("Content-Type", MediaType.TEXT_HTML_VALUE);
    }
}
