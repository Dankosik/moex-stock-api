package ru.dankos.api.moexstockservice.client

import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexMarketdataStockResponse
import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse

@ReactiveFeignClient(name = "moexstocks", url = "\${feign-services.moex-endpoint}")
interface MoexClient {

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping("/{ticker}.json?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST,TIME")
    fun getStockByTicker(@PathVariable("ticker") ticker: String): Mono<MoexMarketdataStockResponse>

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping("/{ticker}.json?iss.only=securities&securities.columns=SECID,PREVPRICE")
    fun getClosedStockPriceByTicker(@PathVariable("ticker") ticker: String): Mono<MoexSecuritiesStockResponse>
}