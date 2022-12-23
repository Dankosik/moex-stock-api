package ru.dankos.api.moexstockservice.client

import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono
import ru.dankos.api.moexstockservice.client.dto.MoexAllStocksResponse
import ru.dankos.api.moexstockservice.client.dto.MoexAllTickersResponse

@HttpExchange
interface MoexTickersClient {

    @GetExchange(".json?iss.dp=comma&iss.meta=off&iss.only=securities&securities.columns=SECID")
    fun getAllAvailableTickers(): Mono<MoexAllTickersResponse>

    @GetExchange(".json?iss.meta=off&iss.json=extended")
    fun getAllStocks(): Mono<List<MoexAllStocksResponse>>
}