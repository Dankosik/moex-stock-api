package ru.dankos.api.moexstockservice.client.dto

data class MoexMarketDataStockResponse(
    val marketdata: MarketData
)

data class MarketData(
    val columns: List<String>,
    val data: List<List<String?>>,
)