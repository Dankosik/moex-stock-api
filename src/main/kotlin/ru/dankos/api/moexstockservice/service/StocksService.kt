package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import mu.KLogging
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.client.MoexTickersClient
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.StockResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.converters.client.toStockResponse
import ru.dankos.api.moexstockservice.model.MoexStockBaseInfo
import java.time.Duration
import java.time.LocalTime

@Service
class StocksService(
    private val cacheableMoexService: CacheableMoexService,
    private val userSubscriptionService: UserSubscriptionService,
    private val moexTickersClient: MoexTickersClient,
) {

    suspend fun getStockPriceByTicker(ticker: String): StockPriceResponse {
        val moexMarketData = cacheableMoexService.getStockMarketInfoByTicker(ticker)
        val moexNotClosed = moexMarketData.stockPrice.value != null
        return if (moexNotClosed) {
            StockPriceResponse(
                ticker = moexMarketData.ticker,
                stockPrice = moexMarketData.stockPrice,
                time = moexMarketData.time
            )
        } else {
            getStockLastPriceByTickerWhenMoexClosed(ticker)
        }
    }

    suspend fun getAllAvailableTickers(): AllTickersResponse =
        cacheableMoexService.getAllAvailableTickers()

    suspend fun getMoexStockBaseInfoByTicker(ticker: String): MoexStockBaseInfo =
        cacheableMoexService.getMoexStockBaseInfoByTicker(ticker)

    suspend fun getMoexStocksByTickers(request: TickersListRequest): List<StockPriceResponse> = coroutineScope {
        request.tickers.map { async { getStockPriceByTicker(it) } }.awaitAll()
    }

    suspend fun pushNotificationWhenPriceIsEqualToSubscription() = coroutineScope {
        val sharedFlow = getAllStocks().shareIn(this, started = SharingStarted.Eagerly)
        getAllAvailableTickers().tickers.forEach { ticker ->
            launch {
                var priceFrom = 0L
                sharedFlow
                    .filter { it.ticker == ticker }
                    .distinctUntilChanged { first, second -> first.stockPrice.value == second.stockPrice.value }
                    .onEach {
                        if (priceFrom == 0L) {
                            priceFrom = it.stockPrice.value!!
                        }
                    }
                    .drop(1)
                    .collect {
                        val priceTo = it.stockPrice.value!!
                        userSubscriptionService.sendNotificationThatPriceReachedSubscription(
                            ticker,
                            priceFrom,
                            priceTo
                        )
                        priceFrom = it.stockPrice.value
                    }
            }
        }
    }

    private suspend fun getStockLastPriceByTickerWhenMoexClosed(ticker: String): StockPriceResponse {
        val moexSecuritiesData = cacheableMoexService.getMoexStockClosedPriceByTicker(ticker)
        return StockPriceResponse(
            ticker = moexSecuritiesData.ticker,
            stockPrice = moexSecuritiesData.stockClosedPrice,
            time = LocalTime.now().minusMinutes(MOEX_DELAY)
        )
    }

    private fun getAllStocks(): Flow<StockResponse> = moexTickersClient.getAllStocks()
        .delaySubscription(Duration.ofSeconds(2))
        .repeat()
        .flatMapIterable { it[1].stocks!! }
        .map { it.toStockResponse() }
        .filter { it.stockPrice.value != null }
        .asFlow()

    companion object : KLogging() {
        private const val MOEX_DELAY: Long = 15
    }
}