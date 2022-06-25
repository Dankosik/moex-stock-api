package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.client.MoexClient
import ru.dankos.api.moexstockservice.config.MoexProperties
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalTime

@Service
class MoexPriceService(
    private val moexClient: MoexClient,
    private val moexProperties: MoexProperties,
    private val cacheStockService: CacheStockService
) {

    suspend fun getStockPriceByTicker(ticker: String): StockPriceResponse {
        val data = cacheStockService.getStockPriceByTicker(ticker).awaitSingle().marketdata.data
        if (data.isEmpty()) throw StockNotFoundException("Stock not found")
        val response = data.first()
        return StockPriceResponse(
            ticker = response[0],
            moneyValue = MoneyValue(
                integer = response[1].toDouble().toInt(),
                fractional = response[1].toBigDecimal().subtract(BigDecimal(response[1].toDouble().toInt()))
                    .toPlainString(),
                currency = moexProperties.api.shares.defaultCurrency
            ),
            time = LocalTime.parse(response[2])
        )
    }

    fun getStockPriceByTickerAsFlow(ticker: String): Flow<StockPriceResponse> {
        val stock = moexClient.getStockByTicker(ticker)
        return stock
            .delaySubscription(Duration.ofSeconds(2))
            .repeat { stock != moexClient.getStockByTicker(ticker) }
            .map { it.marketdata.data.first() }
            .map {
                StockPriceResponse(
                    ticker = it[0],
                    moneyValue = MoneyValue(
                        integer = it[1].toDouble().toInt(),
                        fractional = it[1].toBigDecimal().subtract(BigDecimal(it[1].toDouble().toInt()))
                            .toPlainString(),
                        currency = moexProperties.api.shares.defaultCurrency
                    ),
                    time = LocalTime.parse(it[2]),
                )
            }
            .onErrorMap { throw StockNotFoundException("Stock not found") }
            .asFlow()
            .distinctUntilChangedBy { it.moneyValue }
    }

    suspend fun getMoexStocksByTickers(request: TickersListRequest): List<StockPriceResponse> = coroutineScope {
        request.tickers.map { async { getStockPriceByTicker(it) } }.awaitAll()
    }
}