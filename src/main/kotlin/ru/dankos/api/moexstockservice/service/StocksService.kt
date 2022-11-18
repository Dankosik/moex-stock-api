package ru.dankos.api.moexstockservice.service

import io.netty.channel.ConnectTimeoutException
import io.netty.handler.timeout.ReadTimeoutException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KLogging
import org.springframework.stereotype.Service
import reactor.util.retry.Retry
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.converters.client.toMoexMarketData
import ru.dankos.api.moexstockservice.model.MoexStockBaseInfo
import java.io.IOException
import java.time.Duration
import java.time.LocalTime

@Service
class StocksService(
    private val moexStockClient: MoexStockClient,
    private val cacheableMoexService: CacheableMoexService,
    private val userSubscriptionService: UserSubscriptionService,
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

    private suspend fun getStockLastPriceByTickerWhenMoexClosed(ticker: String): StockPriceResponse {
        val moexSecuritiesData = cacheableMoexService.getMoexStockClosedPriceByTicker(ticker)
        return StockPriceResponse(
            ticker = moexSecuritiesData.ticker,
            stockPrice = moexSecuritiesData.stockClosedPrice,
            time = LocalTime.now().minusMinutes(MOEX_DELAY)
        )
    }

    fun getStockPricesByTickerAsFlow(ticker: String) =
        moexStockClient.getStockByTicker(ticker)
            .delaySubscription(Duration.ofSeconds(1))
            .retryWhen(
                Retry.fixedDelay(100, Duration.ofSeconds(1))
                    .filter { it is IOException || it is ReadTimeoutException || it is ConnectTimeoutException })
            .repeat()
            .filter { it.marketdata.data.all { list-> list.isNotEmpty() } }
            .onErrorContinue { e, _ -> logger.error { e.stackTrace } }
            .map { it.toMoexMarketData() }
            .filter { it.stockPrice.value != null }
            .distinctUntilChanged { it.stockPrice.value }
//            .asFlow()

    suspend fun pushNotificationWhenPriceIsEqualToSubscription() = coroutineScope {
        getAllAvailableTickers().tickers.forEach { ticker ->
            launch {
                var from = 0L
                getStockPricesByTickerAsFlow(ticker)
//                    .onEach {
//                        if (from == 0L) {
//                            from = it.stockPrice.value!!
//                        }
//                        println(it)
//                    }
                    .doOnNext {
                        if (from == 0L) {
                            from = it.stockPrice.value!!
                        }
                    }
                    .log()
                    .skip(1)
//                    .drop(1)
                    .subscribe {
                        GlobalScope.launch {
                            userSubscriptionService.sendNotificationThatPriceReachedSubscription(
                                ticker,
                                from,
                                it.stockPrice.value!!
                            )
                            from = it.stockPrice.value
                        }
                    }
            }
        }
    }

    companion object : KLogging() {
        private const val MOEX_DELAY: Long = 15
        private const val DEFAULT_MINOR_UNITS = 1000000
    }
}