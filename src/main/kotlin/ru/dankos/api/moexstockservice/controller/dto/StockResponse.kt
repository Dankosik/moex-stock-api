package ru.dankos.api.moexstockservice.controller.dto

data class StockResponse(
    val ticker: String,
    val stockPrice: MoneyValue,
)