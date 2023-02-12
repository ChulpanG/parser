package com.test.parser.domain.error;

public sealed interface CreateUrlError {
    record EmptyUrlError() implements CreateUrlError { }
}