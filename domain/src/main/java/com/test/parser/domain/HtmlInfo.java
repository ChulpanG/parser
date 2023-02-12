package com.test.parser.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class HtmlInfo {
    @NonNull
    private Integer reviewsCount;
    @NonNull
    private String name;
    @NonNull
    private Double rating;
    @NonNull
    private String url;
}
