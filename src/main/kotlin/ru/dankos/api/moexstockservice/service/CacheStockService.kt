package ru.dankos.api.moexstockservice.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.MoexClient
import ru.dankos.api.moexstockservice.client.dto.MoexStockResponse

@Service
class CacheStockService(
    private val moexClient: MoexClient,
) {

    @Cacheable(value = ["tickers"], key = "#ticker")
    fun getStockPriceByTicker(ticker: String): Mono<MoexStockResponse> = moexClient.getStockByTicker(ticker).cache()
}