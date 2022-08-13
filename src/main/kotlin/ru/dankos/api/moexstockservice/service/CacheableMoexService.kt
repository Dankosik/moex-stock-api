package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.client.MoexTickersClient
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toAllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toMoexMarketData
import ru.dankos.api.moexstockservice.converters.client.toMoexSecuritiesData
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import ru.dankos.api.moexstockservice.model.MoexMarketInfo
import ru.dankos.api.moexstockservice.model.MoexSecuritiesData

@Service
class CacheableMoexService(
    private val moexStockClient: MoexStockClient,
    private val moexTickersClient: MoexTickersClient,
) {

    @Cacheable(value = ["marketData"])
    suspend fun getStockMarketInfoByTicker(ticker: String): MoexMarketInfo = try {
        moexStockClient.getStockByTicker(ticker).awaitSingle().toMoexMarketData()
    } catch (e: Exception) {
        throw StockNotFoundException("Could not get stock")
            .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
    }

    @Cacheable(value = ["closedMarketData"])
    suspend fun getSecuritiesDataByTicker(ticker: String): MoexSecuritiesData = try {
        moexStockClient.getClosedStockPriceByTicker(ticker).awaitSingle().toMoexSecuritiesData()
    } catch (e: Exception) {
        throw StockNotFoundException("Could not get stock")
            .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
    }

    @Cacheable(value = ["tickers"])
    suspend fun getAllAvailableTickers(): AllTickersResponse =
        moexTickersClient.getAllAvailableTickers().awaitSingle().toAllTickersResponse()
}