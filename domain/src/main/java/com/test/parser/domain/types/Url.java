package com.test.parser.domain.types;

import com.test.parser.domain.exception.InvalidUrlException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url {

    private final String value;
    private static final String REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public Url(String value) {
        this.value = value;
    }

    public String stringValue() {
        return value;
    }

    public static Url from(String url) {
        if (!url.isBlank() && isMatch(url)) {
            return new Url(url);
        } else {
            throw new InvalidUrlException("Invalid url");
        }
    }

    private static boolean isMatch(String s) {
        try {
            Pattern patt = Pattern.compile(Url.REGEX);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }
}