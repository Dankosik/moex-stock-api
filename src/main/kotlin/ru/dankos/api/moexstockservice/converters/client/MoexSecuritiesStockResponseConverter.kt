package ru.dankos.api.moexstockservice.converters.client

import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse
import ru.dankos.api.moexstockservice.converters.convertMoexPriceToMoneyValue
import ru.dankos.api.moexstockservice.model.Exchanges
import ru.dankos.api.moexstockservice.model.MoexStockBaseInfo
import ru.dankos.api.moexstockservice.model.MoexStockClosedPrice

fun MoexSecuritiesStockResponse.toMoexStockClosedPrice() = MoexStockClosedPrice(
    ticker = securities.data[0][0],
    stockClosedPrice = convertMoexPriceToMoneyValue(securities.data[0][1])
)

fun MoexSecuritiesStockResponse.toMoexBaseInfo() = MoexStockBaseInfo(
    ticker = securities.data[0][0],
    exchange = Exchanges.MOEX,
    companyName = securities.data[0][1],
)