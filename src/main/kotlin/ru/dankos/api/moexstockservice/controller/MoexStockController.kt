package ru.dankos.api.moexstockservice.controller

import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.service.MoexPriceService

@RestController
@RequestMapping("/stocks")
class MoexStockController(
    private val moexPriceService: MoexPriceService,
) {

    @GetMapping("/price/{ticker}")
    suspend fun getMoexStockPriceByTicker(@PathVariable ticker: String): StockPriceResponse =
        moexPriceService.getStockPriceByTicker(ticker)

    @GetMapping("/price")
    suspend fun getMoexStocksPriceByTickers(@RequestBody request: TickersListRequest): List<StockPriceResponse> =
        moexPriceService.getMoexStocksByTickers(request)

    @GetMapping(value = ["/subscribe/{ticker}"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun subscribe(@PathVariable ticker: String): Flow<StockPriceResponse> =
        moexPriceService.getStockPriceByTickerAsFlow(ticker)
}