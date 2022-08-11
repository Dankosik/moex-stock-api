package ru.dankos.api.moexstockservice.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.MoexClient
import ru.dankos.api.moexstockservice.client.dto.MoexMarketdataStockResponse
import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse

@Service
class CacheStockService(
    private val moexClient: MoexClient,
) {

    @Cacheable(value = ["marketData"], key = "#ticker")
    fun getStockPriceByTicker(ticker: String): Mono<MoexMarketdataStockResponse> =
        moexClient.getStockByTicker(ticker).cache()

    @Cacheable(value = ["securities"], key = "#ticker")
    fun getClosedStockPriceByTicker(ticker: String): Mono<MoexSecuritiesStockResponse> =
        moexClient.getClosedStockPriceByTicker(ticker).cache()
}