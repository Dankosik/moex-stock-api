package ru.dankos.api.moexstockservice.config

import io.netty.handler.logging.LogLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.transport.logging.AdvancedByteBufFormat
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.client.MoexTickersClient
import java.time.Duration
import java.time.temporal.ChronoUnit


@Configuration
class ClientsConfig {

    @Bean
    fun moexTickersClient(): MoexTickersClient {
        val client = WebClient.builder()
            .baseUrl("https://iss.moex.com/iss/engines/stock/markets/shares/boardgroups/57/securities")
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { it.defaultCodecs().maxInMemorySize(CODEC_MEMORY_SIZE_IN_BYTES) }
                .build()
            )
            .clientConnector(reactorClientHttpConnector())
            .build()
        val proxyFactory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build()
        return proxyFactory.createClient(MoexTickersClient::class.java)
    }

    @Bean
    fun moexStockClient(): MoexStockClient {
        val client = WebClient.builder()
            .baseUrl("https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities")
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { it.defaultCodecs()}
                .build()
            )
            .clientConnector(reactorClientHttpConnector())
            .build()
        val proxyFactory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build()
        return proxyFactory.createClient(MoexStockClient::class.java)
    }

    @Bean
    fun reactorClientHttpConnector(): ReactorClientHttpConnector {
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
        return ReactorClientHttpConnector(httpClient)
    }


    companion object{
        private const val CODEC_MEMORY_SIZE_IN_BYTES = 1024 * 1024
    }
}