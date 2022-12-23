package ru.dankos.api.moexstockservice.config

import io.netty.handler.logging.LogLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.time.Duration
import java.time.temporal.ChronoUnit


@Configuration
class ReactorConfig {

    @Bean
    fun apiClient(): WebClient {
        /*
         * Setting maxIdleTime as 10s, because servers usually have a keepAliveTimeout
         * of 60s, after which the connection gets closed.
         * If the connection pool has any connection which has been idle for over 10s, it
         * will be evicted from the pool.
         * Refer https://github.com/reactor/reactor-netty/issues/1318#issuecomment-702668918
         */
        val connectionProvider = ConnectionProvider.builder("connectionProvider")
            .maxIdleTime(Duration.ofSeconds(10))
            .build()
        val httpClient: HttpClient = HttpClient.create(connectionProvider)
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
            .responseTimeout(Duration.of(5, ChronoUnit.SECONDS))
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}