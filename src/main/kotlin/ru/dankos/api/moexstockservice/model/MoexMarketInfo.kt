package ru.dankos.api.moexstockservice.model

import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import java.time.LocalTime

class MoexMarketInfo(
    val ticker: String,
    val stockPrice: MoneyValue,
    val stockPriceDailyHigh: MoneyValue,
    val stockPriceDailyLow: MoneyValue,
    val stockPriceOpen: MoneyValue,
    val marketCup: MoneyValue,
    val time: LocalTime,
)