package ru.dankos.api.moexstockservice.converters.client

import ru.dankos.api.moexstockservice.client.dto.MoexMarketDataStockResponse
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import ru.dankos.api.moexstockservice.converters.convertMoexPriceToMoneyValue
import ru.dankos.api.moexstockservice.model.MoexMarketData
import java.time.LocalTime

fun MoexMarketDataStockResponse.toMoexMarketData() = MoexMarketData(
    ticker = marketdata.data[0][0]!!,
    stockPrice = convertMoexPriceToMoneyValue(marketdata.data[0][1]),
    stockPriceDailyHigh = convertMoexPriceToMoneyValue(marketdata.data[0][5]),
    stockPriceDailyLow = convertMoexPriceToMoneyValue(marketdata.data[0][4]),
    stockPriceOpen = convertMoexPriceToMoneyValue(marketdata.data[0][3]),
    marketCup = MoneyValue(
        value = marketdata.data[0][6]?.toLong()?.times(100),
        minorUnits = 100,
        currency = "RUR"
    ),
    time = LocalTime.parse(marketdata.data[0][2]),
)