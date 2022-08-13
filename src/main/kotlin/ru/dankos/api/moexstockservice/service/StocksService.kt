package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogging
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.client.MoexStockClient
import ru.dankos.api.moexstockservice.config.MoexProperties
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import java.time.LocalTime

@Service
class StocksService(
    private val moexStockClient: MoexStockClient,
    private val moexProperties: MoexProperties,
    private val cacheableMoexService: CacheableMoexService,
) {

    suspend fun getStockPriceByTicker(ticker: String): StockPriceResponse {
        val moexMarketData = cacheableMoexService.getStockPriceByTicker(ticker).awaitSingle()
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
        cacheableMoexService.getAllAvailableTickers().awaitSingle()

    suspend fun getMoexStocksByTickers(request: TickersListRequest): List<StockPriceResponse> = coroutineScope {
        request.tickers.map { async { getStockPriceByTicker(it) } }.awaitAll()
    }

    private suspend fun getStockLastPriceByTickerWhenMoexClosed(ticker: String): StockPriceResponse {
        val moexSecuritiesData = cacheableMoexService.getClosedStockPriceByTicker(ticker).awaitSingle()
        return StockPriceResponse(
            ticker = moexSecuritiesData.ticker,
            stockPrice =moexSecuritiesData.stockClosedPrice,
            time = LocalTime.now().minusMinutes(MOEX_DELAY)
        )
    }
//    fun getStockPriceByTickerAsFlux(ticker: String): Flux<StockPriceResponse> {
//        val stock = moexStockClient.getStockByTicker(ticker)
//        return stock
//            .delaySubscription(Duration.ofSeconds(2))
//            .repeat { stock != moexStockClient.getStockByTicker(ticker) }
//            .map { it.toMoexMarketData() }
//            .map {
//                StockPriceResponse(
//                    ticker = it.ticker,
//                    stockPrice = it.stockPrice,
//                    time = it.time,
//                )
//            }
//            .onErrorMap {
//                throw StockNotFoundException("Stock not found")
//                    .apply { logger.warn { "Could not get stock by ticker: $ticker" } }
//            }
//            .distinctUntilChanged { it.stockPrice }
//    }

//    private fun getSumStocks(stocks: List<MoneyValue>): MoneyValue {
//        var sumValue: Long = 0
//        stocks.stream()
//            .forEach { stock ->
//                sumValue += calculateMinorUnitsToHundred(stock).value
//            }
//        return MoneyValue(sumValue / 1000, 100, stocks[0].currency)
//    }
//
//    private fun calculateMinorUnitsToHundred(moneyValue: MoneyValue): MoneyValue {
//        val millionMinorUnit = moneyValue.minorUnits.toString().padEnd(7, '0').toInt()
//        val addingZerosToValue = millionMinorUnit.div(moneyValue.minorUnits)
//        val valueWithMillionMinorUnit = (moneyValue.value.toString() + addingZerosToValue.toString().drop(1)).toLong()
//        return MoneyValue(valueWithMillionMinorUnit, millionMinorUnit, moneyValue.currency)
//    }

    companion object : KLogging() {
        private const val POINT = '.'
        private const val MOEX_DELAY: Long = 15
        private const val DEFAULT_MINOR_UNITS = 1000000
    }
}