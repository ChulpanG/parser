package com.test.parser.domain.types;

import com.github.javafaker.Faker;
import com.test.parser.domain.error.CreateUrlError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlTest {

    private static final Faker faker = new Faker();

    @Test
    void createdUrlSuccessfully() {
        var url = "https://" + faker.internet().url();
        var res = Url.from(url);
        Assertions.assertTrue(res.isRight());
        Assertions.assertEquals(url, res.get().stringValue());
    }

    @Test
    void creatingUrlFailed() {
        var url = faker.internet().uuid();
        var res = Url.from(url);
        Assertions.assertTrue(res.isLeft());
        Assertions.assertInstanceOf(CreateUrlError.class, res.getLeft());
    }
}
