package ru.dankos.api.moexstockservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse
import ru.dankos.api.moexstockservice.controller.dto.StockPriceResponse
import ru.dankos.api.moexstockservice.controller.dto.TickersListRequest
import ru.dankos.api.moexstockservice.model.MoexStockBaseInfo
import ru.dankos.api.moexstockservice.service.StocksService

@RestController
@RequestMapping("/stocks")
class MoexStockController(
    private val stocksService: StocksService,
) {

    @GetMapping("/{ticker}/price")
    suspend fun getMoexStockPriceByTicker(@PathVariable ticker: String): StockPriceResponse =
        stocksService.getStockPriceByTicker(ticker)

    @GetMapping("/{ticker}/baseInfo")
    suspend fun getMoexStockBaseInfoByTicker(@PathVariable ticker: String): MoexStockBaseInfo =
        stocksService.getMoexStockBaseInfoByTicker(ticker)

    @GetMapping("/price")
    suspend fun getMoexStocksPriceByTickers(@RequestBody request: TickersListRequest): List<StockPriceResponse> =
        stocksService.getMoexStocksByTickers(request)

    @GetMapping("/tickers")
    suspend fun getAllAvailableTickers(): AllTickersResponse =
        stocksService.getAllAvailableTickers()

//    @GetMapping(value = ["/{ticker}/subscribe"])
//    fun subscribe(@PathVariable ticker: String): Flux<StockPriceResponse> =
//        moexPriceService.getStockPriceByTickerAsFlux(ticker)
}