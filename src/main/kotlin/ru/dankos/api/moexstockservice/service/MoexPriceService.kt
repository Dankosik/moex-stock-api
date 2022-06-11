package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.client.MoexClient
import ru.dankos.api.moexstockservice.config.MoexProperties
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import java.math.BigDecimal

@Service
class MoexPriceService(
    private val moexClient: MoexClient,
    private val moexProperties: MoexProperties
) {

    suspend fun getStockPriceByTicker(ticker: String): StockPriceResponse {
        val data = moexClient.getStock(ticker).awaitSingle().marketdata.data
        if (data.isEmpty()) throw StockNotFoundException("Stock not found")
        val response = data.first()
        return StockPriceResponse(
            ticker = response[0],
            moneyValue = MoneyValue(
                integer = response[1].toDouble().toInt(),
                fractional = response[1].toBigDecimal().subtract(BigDecimal(response[1].toDouble().toInt()))
                    .toPlainString(),
                currency = moexProperties.api.shares.defaultCurrency
            )
        )
    }

    suspend fun getMoexStocksByTickers(request: TickersListRequest): List<StockPriceResponse> = coroutineScope {
        request.tickers.map { async { getStockPriceByTicker(it) } }.awaitAll()
    }
}