package ru.dankos.api.moexstockservice.client.dto

class MoexStockResponse(
    val marketdata: Marketdata
)

class Marketdata(
    val columns: List<String>,
    val data: List<List<String>>,
)