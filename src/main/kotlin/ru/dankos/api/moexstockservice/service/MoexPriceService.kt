package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.dankos.api.moexstockservice.client.MoexClient
import ru.dankos.api.moexstockservice.config.MoexProperties
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import java.time.Duration
import java.time.LocalTime
import kotlin.math.pow

@Service
class MoexPriceService(
    private val moexClient: MoexClient,
    private val moexProperties: MoexProperties,
    private val cacheStockService: CacheStockService
) {

    suspend fun getStockPriceByTicker(ticker: String): StockPriceResponse {
        val data = cacheStockService.getStockPriceByTicker(ticker).awaitSingle().marketdata.data
        if (data.isEmpty()) {
            throw StockNotFoundException("Stock not found")
                .apply { logger.warn { "Could not get stock by ticker: $ticker" } }
        }
        val response = data.first()
        val moexNotClosed = response[1] != null
        return if (moexNotClosed) {
            val exchangeUnit = 10.0.pow(countDigitsAfterPoint(response[1]!!).toDouble()).toInt()
            StockPriceResponse(
                ticker = response[0]!!,
                moneyValue = MoneyValue(
                    value = (response[1]!!.toDouble() * exchangeUnit).toInt(),
                    minorUnits = exchangeUnit,
                    currency = moexProperties.api.shares.defaultCurrency
                ),
                time = LocalTime.parse(response[2])
            )
        } else {
            getStockLastPriceByTickerWhenMoexClosed(ticker)
        }
    }

    fun getStockPriceByTickerAsFlux(ticker: String): Flux<StockPriceResponse> {
        val stock = moexClient.getStockByTicker(ticker)
        return stock
            .delaySubscription(Duration.ofSeconds(2))
            .repeat { stock != moexClient.getStockByTicker(ticker) }
            .map { it.marketdata.data.first() }
            .map {
                StockPriceResponse(
                    ticker = it[0]!!,
                    moneyValue = MoneyValue(
                        value = (it[1]!!.toDouble() * 10.0.pow(countDigitsAfterPoint(it[1]!!).toDouble())
                            .toInt()).toInt(),
                        minorUnits = 10.0.pow(countDigitsAfterPoint(it[1]!!).toDouble()).toInt(),
                        currency = moexProperties.api.shares.defaultCurrency
                    ),
                    time = LocalTime.parse(it[2]),
                )
            }
            .onErrorMap {
                throw StockNotFoundException("Stock not found")
                    .apply { logger.warn { "Could not get stock by ticker: $ticker" } }
            }
            .distinctUntilChanged { it.moneyValue }
    }

    suspend fun getMoexStocksByTickers(request: TickersListRequest): List<StockPriceResponse> = coroutineScope {
        request.tickers.map { async { getStockPriceByTicker(it) } }.awaitAll()
    }

    private suspend fun getStockLastPriceByTickerWhenMoexClosed(ticker: String): StockPriceResponse {
        val response = cacheStockService.getClosedStockPriceByTicker(ticker).awaitSingle().securities.data.first()
        val exchangeUnit = 10.0.pow(countDigitsAfterPoint(response[1]).toDouble()).toInt()
        return StockPriceResponse(
            ticker = response[0],
            moneyValue = MoneyValue(
                value = (response[1].toDouble() * exchangeUnit).toInt(),
                minorUnits = exchangeUnit,
                currency = moexProperties.api.shares.defaultCurrency
            ),
            time = LocalTime.now().minusMinutes(MOEX_DELAY)
        )
    }

    private fun countDigitsAfterPoint(number: String) =
        number.reversed().chars().takeWhile { it.toChar() != POINT }.count()

    companion object : KLogging() {
        private const val POINT = '.'
        private const val MOEX_DELAY: Long = 15
    }
}