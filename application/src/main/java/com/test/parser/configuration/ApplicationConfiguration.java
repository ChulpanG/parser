package com.test.parser.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ReactorClientHttpConnector reactorClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection -> connection
                        .addHandlerFirst(new ReadTimeoutHandler(10))
                        .addHandlerFirst(new WriteTimeoutHandler(10))
                )
        );
    }

    @Bean
    WebClient webClient() {
        var size = 16 * 1024 * 1024;
        var strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector())
                .exchangeStrategies(strategies)
                .build();
    }
}
