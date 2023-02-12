package com.test.parser.service;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.error.ParseHtmlError;
import io.vavr.control.Either;
import reactor.core.publisher.Mono;

public interface ParsePageService {

    Mono<Either<ParseHtmlError, HtmlInfo>> execute(Url url);
}