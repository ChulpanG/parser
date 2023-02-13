package com.test.parser.service;

import com.test.parser.domain.HtmlInfo;
import com.test.parser.domain.types.Url;
import reactor.core.publisher.Mono;

public interface ParsePageService {

    Mono<HtmlInfo> execute(Url url);
}