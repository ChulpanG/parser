package com.test.parser.configuration;

import com.test.parser.service.ParseHtml;
import com.test.parser.service.implementation.ParseHtmlImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ParseHtml parseHtml() {
        return new ParseHtmlImpl();
    }
}
