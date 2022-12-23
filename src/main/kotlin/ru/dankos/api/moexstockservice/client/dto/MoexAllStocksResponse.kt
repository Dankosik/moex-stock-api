package ru.dankos.api.moexstockservice.client.dto

import com.fasterxml.jackson.annotation.JsonAlias

data class MoexAllStocksResponse(
    @field:JsonAlias("marketdata")
    val stocks: List<Stock>? = null
)

data class Stock(
    @field:JsonAlias("SECID")
    val ticker: String,
    @field:JsonAlias("LAST")
    val lastPrice: Double? = null,
)
