package ru.dankos.api.moexstockservice.client.dto

data class MoexStockResponse(
    val marketdata: Marketdata
)

data class Marketdata(
    val columns: List<String>,
    val data: List<List<String>>,
)