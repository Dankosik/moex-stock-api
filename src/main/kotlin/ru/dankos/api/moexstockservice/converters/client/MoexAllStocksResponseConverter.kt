package ru.dankos.api.moexstockservice.converters.client

import ru.dankos.api.moexstockservice.client.dto.Stock
import ru.dankos.api.moexstockservice.controller.dto.StockResponse
import ru.dankos.api.moexstockservice.converters.convertMoexPriceToMoneyValue

fun Stock.toStockResponse() = StockResponse(
    ticker = ticker,
    stockPrice = convertMoexPriceToMoneyValue(lastPrice.toString())
)