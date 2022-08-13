package ru.dankos.api.moexstockservice.controller.dto

import java.time.LocalTime

data class StockPriceResponse(
    val ticker: String,
    val stockPrice: MoneyValue,
    val time: LocalTime,
)

data class MoneyValue(
    val value: Long? = null,
    val minorUnits: Int? = null,
    val currency: String? = null,
)