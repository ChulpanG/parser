package com.test.parser.service.implementation;

import com.test.parser.domain.types.Url;
import com.test.parser.service.error.ParseHtmlError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseHtmlImplTest {

    private static final String HTML_URL = "https://www.sitejabber.com/reviews/keh.com";

    @Test
    void parseHtml() {
        var service = new ParseHtmlImpl();
        var url = Url.from(HTML_URL).get();
        var result = service.execute(url);
        Assertions.assertTrue(result.isRight());
        Assertions.assertEquals(result.get().getName(), "KEH");
        Assertions.assertEquals(result.get().getUrl(), "www.keh.com");
        Assertions.assertNotNull(result.get().getReviewsCount());
        Assertions.assertNotNull(result.get().getRating());
    }

    @Test
    void parseHtml_ConnectError() {
        var service = new ParseHtmlImpl();
        var url = Url.from(HTML_URL + "lol").get();
        var result = service.execute(url);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertInstanceOf(ParseHtmlError.ConnectToPageError.class, result.getLeft());
    }

    @Test
    void parseHtml_PageParsingError() {
        var service = new ParseHtmlImpl();
        var url = Url.from("https://www.sitejabber.com/").get();
        var result = service.execute(url);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertInstanceOf(ParseHtmlError.PageParsingError.class, result.getLeft());
    }
}
