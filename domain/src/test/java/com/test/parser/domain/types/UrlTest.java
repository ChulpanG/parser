package com.test.parser.domain.types;

import com.github.javafaker.Faker;
import com.test.parser.domain.exception.InvalidUrlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlTest {

    private static final Faker faker = new Faker();

    @Test
    void createdUrlSuccessfully() {
        var url = "https://" + faker.internet().url();
        var res = Url.from(url);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(url, res.stringValue());
    }

    @Test
    void creatingUrlFailed() {
        var url = faker.internet().uuid();
        InvalidUrlException th = Assertions.assertThrows(InvalidUrlException.class, () -> Url.from(url));
        Assertions.assertEquals("Invalid url", th.getMessage());
    }
}
