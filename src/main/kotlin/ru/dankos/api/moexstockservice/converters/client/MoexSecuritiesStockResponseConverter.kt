package ru.dankos.api.moexstockservice.converters.client

import ru.dankos.api.moexstockservice.client.dto.MoexSecuritiesStockResponse
import ru.dankos.api.moexstockservice.converters.convertMoexPriceToMoneyValue
import ru.dankos.api.moexstockservice.model.MoexSecuritiesData

fun MoexSecuritiesStockResponse.toMoexSecuritiesData() = MoexSecuritiesData(
    ticker = this.securities.data[0][0],
    companyShortName = this.securities.data[0][2],
    companyFullName = this.securities.data[0][3],
    stockClosedPrice = convertMoexPriceToMoneyValue(this.securities.data[0][1])
)