package ru.dankos.api.moexstockservice.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Ticker
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CaffeineConfig(
    private val caffeineProperties: CaffeineProperties
) {

    @Bean
    fun ticker(): Ticker = Ticker.systemTicker()

    @Bean
    fun cacheManager(ticker: Ticker): CacheManager {
        val manager = SimpleCacheManager()
        val caches = caffeineProperties.cacheNames.entries
            .map { (key, value): Map.Entry<String, CacheSpec> ->
                buildCache(
                    key,
                    value,
                    ticker
                )
            }.toList()
        manager.setCaches(caches)
        return manager
    }

    private fun buildCache(name: String, cacheSpec: CacheSpec, ticker: Ticker): CaffeineCache {
        val caffeineBuilder = Caffeine.newBuilder()
            .expireAfterWrite(cacheSpec.timeout, TimeUnit.SECONDS)
            .ticker(ticker)
        return CaffeineCache(name, caffeineBuilder.build())
    }
}