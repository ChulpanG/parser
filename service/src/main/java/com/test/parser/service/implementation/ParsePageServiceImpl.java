package com.test.parser.service.implementation;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParsePageService;
import com.test.parser.service.error.ParseHtmlError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class ParsePageServiceImpl implements ParsePageService {

    private final String RATING_REVIEWS_COUNT_CLASSES = "rating__reviews__count";
    private final String NAME_CLASSES = "url-header__info__title url-header__info__title--badge";
    private final String URL_CLASSES = "url-header__external-link";
    private final String RATING_CLASSES = "stars stars stars-container stars-container--large";
    private final String RATING_ATTRIBUTE = "data-rating";

    private final WebClient webClient;

    public ParsePageServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Either<ParseHtmlError, HtmlInfo>> execute(Url url) {
        return webClient.get().uri(url.stringValue()).retrieve()
                .bodyToMono(String.class)
                .flatMap(html -> {
                    var document = Jsoup.parse(html);
                    var reviewsCount = !document
                            .getElementsByClass(RATING_REVIEWS_COUNT_CLASSES)
                            .text().isBlank() ? document
                            .getElementsByClass(RATING_REVIEWS_COUNT_CLASSES)
                            .text().replaceAll(",", "") : null;
                    var name =
                            !document
                                    .getElementsByClass(NAME_CLASSES)
                                    .text().isBlank() ? document
                                    .getElementsByClass(NAME_CLASSES)
                                    .text() : null;
                    var urlName =
                            !document
                                    .getElementsByClass(URL_CLASSES).isEmpty() ?
                                    Objects.requireNonNull(document
                                                    .getElementsByClass(URL_CLASSES).first())
                                            .text() : null;
                    var rating =
                            !document
                                    .getElementsByClass(RATING_CLASSES)
                                    .isEmpty() ? Objects.requireNonNull(document
                                            .getElementsByClass(RATING_CLASSES)
                                            .first())
                                    .attr(RATING_ATTRIBUTE) : null;
                    var htmlInfo = Try.of(() ->
                                    HtmlInfo.builder()
                                            .reviewsCount(Integer.valueOf(reviewsCount))
                                            .name(name)
                                            .rating(Double.valueOf(rating))
                                            .url(urlName)
                                            .build())
                            .toEither()
                            .mapLeft(e -> new ParseHtmlError.InvalidPageStructureError(e.getMessage(), url.stringValue()));
                    if (htmlInfo.isRight()) {
                        return Mono.just(Either.right(htmlInfo.get()));
                    } else {
                        return Mono.just(Either.left(htmlInfo.getLeft()));
                    }
                });
    }
}
