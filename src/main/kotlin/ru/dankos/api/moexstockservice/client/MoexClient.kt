package ru.dankos.api.moexstockservice.client

import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexStockResponse

@ReactiveFeignClient(name = "moexstocks", url = "\${moex.api.shares.url}")
interface MoexClient {

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping("/{ticker}.json?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST,TIME")
    fun getStockByTicker(@PathVariable("ticker") ticker: String): Mono<MoexStockResponse>
}