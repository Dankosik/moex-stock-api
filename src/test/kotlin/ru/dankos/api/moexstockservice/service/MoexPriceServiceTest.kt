package ru.dankos.api.moexstockservice.service

import org.junit.jupiter.api.Test
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue

internal class MoexPriceServiceTest {

    @Test
    fun `test sum two stock with cost more than 1 rub`() {
        val tatnList = arrayListOf(
            MoneyValue(41360, 100, "RUR"),
            MoneyValue(41915, 100, "RUR")
        )
        assert(getSumStocks(tatnList) == MoneyValue(83275, 100, "RUR"))
    }

    @Test
    fun `test sum five stock with cost more than 1 rub`() {
        val msngList = arrayListOf(
            MoneyValue(178, 100, "RUR"),
            MoneyValue(221, 100, "RUR"),
            MoneyValue(159, 100, "RUR"),
            MoneyValue(357, 100, "RUR"),
            MoneyValue(171, 100, "RUR")
        )
        assert(getSumStocks(msngList) == MoneyValue(1086, 100, "RUR"))
    }

    @Test
    fun `test sum five stock with cost more than 10 rub`() {
        val afksList = arrayListOf(
            MoneyValue(14369, 1000, "RUR"),
            MoneyValue(15472, 1000, "RUR"),
            MoneyValue(14963, 1000, "RUR"),
            MoneyValue(14573, 1000, "RUR"),
            MoneyValue(16783, 1000, "RUR")
        )
        assert(getSumStocks(afksList) == MoneyValue(7616, 100, "RUR"))
    }

    @Test
    fun `test sum five stock with cost more than 100 rub`() {
        val sberList = arrayListOf(
            MoneyValue(12447, 100, "RUR"),
            MoneyValue(12547, 100, "RUR"),
            MoneyValue(12547, 100, "RUR"),
            MoneyValue(16447, 100, "RUR"),
            MoneyValue(12547, 100, "RUR")
        )
        assert(getSumStocks(sberList) == MoneyValue(66535, 100, "RUR"))
    }

    private fun getSumStocks(stocks: List<MoneyValue>): MoneyValue {
        var sumValue = 0
        stocks.stream()
            .forEach { stock ->
                sumValue += getDefaultShapeForMoneyValue(stock).value
            }
        return MoneyValue(sumValue / 10000, 100, stocks[0].currency)
    }

    private fun getDefaultShapeForMoneyValue(moneyValue: MoneyValue): MoneyValue {
        val millionMinorUnit = moneyValue.minorUnits.toString().padEnd(7, '0').toInt()
        val addingZerosToValue = millionMinorUnit.div(moneyValue.minorUnits)
        val valueWithMillionMinorUnit = (moneyValue.value.toString() + addingZerosToValue.toString().drop(1)).toInt()
        return MoneyValue(valueWithMillionMinorUnit, millionMinorUnit, moneyValue.currency)
    }

}