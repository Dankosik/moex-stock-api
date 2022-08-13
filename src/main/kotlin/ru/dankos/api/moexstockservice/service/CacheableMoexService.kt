package ru.dankos.api.moexstockservice.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.client.MoexTickersClient
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toAllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toMoexMarketData
import ru.dankos.api.moexstockservice.converters.client.toMoexSecuritiesData
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import ru.dankos.api.moexstockservice.model.MoexMarketData
import ru.dankos.api.moexstockservice.model.MoexSecuritiesData

@Service
class CacheableMoexService(
    private val moexStockClient: MoexStockClient,
    private val moexTickersClient: MoexTickersClient,
) {

    @Cacheable(value = ["marketData"], key = "#ticker")
    fun getStockPriceByTicker(ticker: String): Mono<MoexMarketData> =
        moexStockClient.getStockByTicker(ticker)
            .map { it.toMoexMarketData() }
            .onErrorMap {
                throw StockNotFoundException("Could not get stock")
                    .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
            }
            .cache()

    @Cacheable(value = ["closedMarketData"], key = "#ticker")
    fun getClosedStockPriceByTicker(ticker: String): Mono<MoexSecuritiesData> =
        moexStockClient.getClosedStockPriceByTicker(ticker)
            .map { it.toMoexSecuritiesData() }
            .onErrorMap {
                throw StockNotFoundException("Could not get stock")
                    .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
            }
            .cache()

    @Cacheable(value = ["tickers"], key = "#root.target.STATIC_TICKERS_KEY")
    suspend fun getAllAvailableTickers(): Mono<AllTickersResponse> =
        moexTickersClient.getAllAvailableTickers().map { it.toAllTickersResponse() }.cache()

    companion object{
        const val STATIC_TICKERS_KEY = "TICKERS"
    }
}