package ru.dankos.api.moexstockservice.client

import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexMarketDataStockResponse
import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse

@ReactiveFeignClient(name = "moexstocks", url = "\${feign-services.moex-stocks-endpoint}")
interface MoexStockClient {

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping("/{ticker}.json?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST,TIME,OPEN,LOW,HIGH,ISSUECAPITALIZATION")
    fun getStockByTicker(@PathVariable("ticker") ticker: String): Mono<MoexMarketDataStockResponse>

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping("/{ticker}.json?iss.only=securities&securities.columns=SECID,PREVPRICE,SHORTNAME,SECNAME")
    fun getClosedStockPriceByTicker(@PathVariable("ticker") ticker: String): Mono<MoexSecuritiesStockResponse>
}