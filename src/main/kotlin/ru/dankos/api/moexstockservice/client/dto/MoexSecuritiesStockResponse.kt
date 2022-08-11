package ru.dankos.api.moexstockservice.client.dto

data class MoexSecuritiesStockResponse(
    val securities: Securities
)

data class Securities(
    val columns: List<String>,
    val data: List<List<String>>,
)