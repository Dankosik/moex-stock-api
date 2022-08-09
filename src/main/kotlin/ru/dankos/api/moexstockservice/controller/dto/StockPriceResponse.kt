package ru.dankos.api.moexstockservice.controller.dto

import java.time.LocalTime

data class StockPriceResponse(
    val ticker: String,
    val moneyValue: MoneyValue,
    val time: LocalTime,
)

data class MoneyValue(
    val value: Int,
    val minorUnits: Int,
    val currency: String,
)