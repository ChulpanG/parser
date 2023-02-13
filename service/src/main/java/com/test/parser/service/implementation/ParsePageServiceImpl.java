package com.test.parser.service.implementation;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import com.test.parser.service.ParsePageService;
import com.test.parser.service.exception.ConnectPageUnexpectedException;
import com.test.parser.service.exception.InvalidPageStructureException;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatusCode;
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
    public Mono<HtmlInfo> execute(Url url) {
        return webClient.get().uri(url.stringValue())
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(Throwable.class).map(ConnectPageUnexpectedException::new)
                )
                .bodyToMono(String.class)
                .flatMap(html -> {
                    HtmlInfo htmlInfo;
                    try {
                        var document = Jsoup.parse(html);
                        var reviewsCount = document.getElementsByClass(RATING_REVIEWS_COUNT_CLASSES)
                                .text().replaceAll(",", "");
                        var name = document.getElementsByClass(NAME_CLASSES).text();
                        var urlName = Objects.requireNonNull(document
                                .getElementsByClass(URL_CLASSES).first()).text();
                        var rating = Objects.requireNonNull(document
                                .getElementsByClass(RATING_CLASSES).first()).attr(RATING_ATTRIBUTE);
                        htmlInfo = HtmlInfo.builder()
                                .reviewsCount(!reviewsCount.isBlank() ? Integer.valueOf(reviewsCount) : null)
                                .name(!name.isBlank() ? name : null)
                                .rating(!rating.isBlank() ? Double.valueOf(rating) : null)
                                .url(!urlName.isBlank() ? urlName : null)
                                .build();
                    } catch (Exception e) {
                        throw new InvalidPageStructureException(e);
                    }
                    return Mono.just(htmlInfo);
                });
    }
}
