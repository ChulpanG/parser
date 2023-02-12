package com.test.parser.service.implementation;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParseHtml;
import com.test.parser.service.error.ParseHtmlError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.jsoup.Jsoup;

import java.util.Objects;

public class ParseHtmlImpl implements ParseHtml {

    @Override
    public Either<ParseHtmlError, HtmlInfo> execute(Url url) {
        var document = Try.of(() -> Jsoup.connect(url.stringValue()).get()).toEither()
                .mapLeft(e -> new ParseHtmlError.ConnectToPageError(e.getMessage(), url.stringValue()));
        if (document.isRight()) {
            var reviewsCount = !document.get()
                    .getElementsByClass("rating__reviews__count")
                    .text().isBlank() ? document.get()
                    .getElementsByClass("rating__reviews__count")
                    .text().replaceAll(",", "") : null;
            var name =
                    !document.get()
                            .getElementsByClass("url-header__info__title url-header__info__title--badge")
                            .text().isBlank() ? document.get()
                            .getElementsByClass("url-header__info__title url-header__info__title--badge")
                            .text() : null;
            var urlName =
                    !document.get()
                            .getElementsByClass("url-header__external-link").isEmpty() ?
                            Objects.requireNonNull(document.get()
                                            .getElementsByClass("url-header__external-link").first())
                                    .text() : null;
            var rating =
                    !document.get()
                            .getElementsByClass("stars stars stars-container stars-container--large")
                            .isEmpty() ? Objects.requireNonNull(document.get()
                                    .getElementsByClass("stars stars stars-container stars-container--large")
                                    .first())
                            .attr("data-rating") : null;
            var htmlInfo = Try.of(() ->
                            HtmlInfo.builder()
                                    .reviewsCount(Integer.valueOf(reviewsCount))
                                    .name(name)
                                    .rating(Double.valueOf(rating))
                                    .url(urlName)
                                    .build())
                    .toEither()
                    .mapLeft(e -> new ParseHtmlError.PageParsingError(e.getMessage(), url.stringValue()));
            if (htmlInfo.isRight()) {
                return Either.right(htmlInfo.get());
            } else {
                return Either.left(htmlInfo.getLeft());
            }
        } else {
            return Either.left(document.getLeft());
        }
    }
}
