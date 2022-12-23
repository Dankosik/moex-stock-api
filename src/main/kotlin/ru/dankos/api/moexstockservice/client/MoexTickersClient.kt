package ru.dankos.api.moexstockservice.client

import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.web.bind.annotation.GetMapping
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexAllStocksResponse
import ru.dankos.api.moexstockservice.client.dto.MoexAllTickersResponse

@ReactiveFeignClient(name = "moextickers", url = "\${feign-services.moex-tickers-endpoint}")
interface MoexTickersClient {

    @CollectionFormat(feign.CollectionFormat.CSV)
    @GetMapping(".json?iss.dp=comma&iss.meta=off&iss.only=securities&securities.columns=SECID")
    fun getAllAvailableTickers(): Mono<MoexAllTickersResponse>

    @GetMapping(".json?iss.meta=off&iss.json=extended")
    fun getAllStocks(): Mono<List<MoexAllStocksResponse>>
}