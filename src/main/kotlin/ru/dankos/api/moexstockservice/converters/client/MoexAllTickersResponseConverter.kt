package ru.dankos.api.moexstockservice.converters.client

import ru.dankos.api.moexstockservice.client.dto.MoexAllTickersResponse
import ru.dankos.api.moexstockservice.controller.dto.AllTickersResponse

fun MoexAllTickersResponse.toAllTickersResponse() = AllTickersResponse(
    tickers = securities.data.flatten().filterNotNull()
)