package ru.dankos.api.moexstockservice.model

class MoexStockBaseInfo(
    val ticker: String,
    val companyName: String,
    val exchange: Exchanges,
)