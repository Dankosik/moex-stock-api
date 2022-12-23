package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.util.retry.Retry
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.client.MoexTickersClient
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toAllTickersResponse
import ru.dankos.api.moexstockservice.converters.client.toMoexBaseInfo
import ru.dankos.api.moexstockservice.converters.client.toMoexMarketData
import ru.dankos.api.moexstockservice.converters.client.toMoexStockClosedPrice
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import ru.dankos.api.moexstockservice.model.MoexMarketInfo
import ru.dankos.api.moexstockservice.model.MoexStockBaseInfo
import ru.dankos.api.moexstockservice.model.MoexStockClosedPrice
import java.time.Duration

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

    @Cacheable(value = ["closedStockPrice"])
    suspend fun getMoexStockClosedPriceByTicker(ticker: String): MoexStockClosedPrice = try {
        moexStockClient.getClosedStockPriceByTicker(ticker).awaitSingle().toMoexStockClosedPrice()
    } catch (e: Exception) {
        throw StockNotFoundException("Could not get stock")
            .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
    }

    @Cacheable(value = ["baseInfo"])
    suspend fun getMoexStockBaseInfoByTicker(ticker: String): MoexStockBaseInfo = try {
        moexStockClient.getStockBaseInfo(ticker).awaitSingle().toMoexBaseInfo()
    } catch (e: Exception) {
        throw StockNotFoundException("Could not get stock")
            .apply { StocksService.logger.warn { "Could not get stock by ticker: $ticker" } }
    }

    @Cacheable(value = ["tickers"])
    suspend fun getAllAvailableTickers(): AllTickersResponse =
        moexTickersClient.getAllAvailableTickers()
            .retryWhen(Retry.backoff(20, Duration.ofSeconds(2)).filter { it is Exception })
            .awaitSingle().toAllTickersResponse()
}