package com.test.parser.service;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.error.ParseHtmlError;
import io.vavr.control.Either;

public interface ParseHtml {
    Either<ParseHtmlError, HtmlInfo> execute(Url url);
}