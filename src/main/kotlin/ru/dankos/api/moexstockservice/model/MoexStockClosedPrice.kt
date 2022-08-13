package ru.dankos.api.moexstockservice.model

import ru.dankos.api.moexstockservice.controller.dto.MoneyValue

class MoexStockClosedPrice(
    val ticker: String,
    val stockClosedPrice: MoneyValue,
)