package ru.dankos.api.moexstockservice.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue

internal class MoexPriceServiceTest {

    @Test
    fun getSumStocks() {
        val feesList = arrayListOf(
            MoneyValue(905, 10000, "RUR"),
            MoneyValue(9004, 100000, "RUR")
        )
        val tatnList = arrayListOf(
            MoneyValue(41360, 100, "RUR"),
            MoneyValue(41915, 100, "RUR")
        )
        val yndxAndVtbrList = arrayListOf(
            MoneyValue(199860, 100, "RUR"),
            MoneyValue(18135, 1000000, "RUR")
        )
        assertAll(
            { assert(getSumStocks(feesList) == MoneyValue(180540, 1000000, "RUR")) },
            { assert(getSumStocks(tatnList) == MoneyValue(832750000, 1000000, "RUR")) },
            { assert(getSumStocks(yndxAndVtbrList) == MoneyValue(1998618135, 1000000, "RUR")) }
        )
    }

    private fun getSumStocks(stocksList: List<MoneyValue>): MoneyValue {
        var sumValue = 0
        stocksList.stream()
            .forEach { stock ->
                val minorUnitMillion = stock.minorUnits.toString().padEnd(7, '0').toInt()
                val addingZerosToValue = minorUnitMillion.div(stock.minorUnits)
                val valueMinorUnit = (stock.value.toString() + addingZerosToValue.toString().drop(1)).toInt()
                sumValue += valueMinorUnit
            }
        return MoneyValue(sumValue, 1000000, stocksList[0].currency)
    }
}