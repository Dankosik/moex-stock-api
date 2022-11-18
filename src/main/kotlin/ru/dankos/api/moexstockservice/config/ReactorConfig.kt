package ru.dankos.api.moexstockservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.resources.ConnectionProvider
import java.time.Duration


@Configuration
class ReactorConfig {
    @Bean
    fun connectionProvider() = ConnectionProvider.builder("fixed")
        .maxIdleTime(Duration.ofSeconds(1))
        .maxConnections(1024)
        .maxLifeTime(Duration.ofSeconds(1))
        .pendingAcquireTimeout(Duration.ofSeconds(-1))
        .pendingAcquireMaxCount(-1)
        .build()
}