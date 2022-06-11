package ru.dankos.api.moexstockservice.controller.dto

class StockPriceResponse(
    val ticker: String,
    val moneyValue: MoneyValue,
)

data class MoneyValue(
    val integer: Int,
    val fractional: String,
    val currency: String,
)