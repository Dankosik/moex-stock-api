package ru.dankos.api.moexstockservice.client

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexMarketDataStockResponse
import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse

@HttpExchange
interface MoexStockClient {

    @GetExchange("/{ticker}.json?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST,TIME,OPEN,LOW,HIGH,ISSUECAPITALIZATION")
    fun getStockByTicker(@PathVariable("ticker") ticker: String): Mono<MoexMarketDataStockResponse>

    @GetExchange("/{ticker}.json?iss.only=securities&securities.columns=SECID,PREVPRICE,SHORTNAME,SECNAME")
    fun getClosedStockPriceByTicker(@PathVariable("ticker") ticker: String): Mono<MoexSecuritiesStockResponse>

    @GetExchange("/{ticker}.json?iss.only=securities&securities.columns=SECID,SECNAME")
    fun getStockBaseInfo(@PathVariable("ticker") ticker: String): Mono<MoexSecuritiesStockResponse>
}