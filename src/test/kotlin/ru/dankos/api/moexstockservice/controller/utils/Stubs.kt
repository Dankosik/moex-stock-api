package ru.dankos.api.moexstockservice.controller.utils

import com.github.tomakehurst.wiremock.client.WireMock
import java.io.File

fun stubForStockFromMoex(ticker: String, bodyFilePath: String) {
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/iss/engines/stock/markets/shares/boards/TQBR/securities/$ticker.json?iss.only=marketdata&iss.meta=off&marketdata.columns=SECID%2CLAST%2CTIME"))
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json;")
                    .withBody(File(bodyFilePath).readText(Charsets.UTF_8))
            )
    )
}