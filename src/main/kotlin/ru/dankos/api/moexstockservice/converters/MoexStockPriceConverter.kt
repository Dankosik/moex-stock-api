package ru.dankos.api.moexstockservice.converters

import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import kotlin.math.pow

fun convertMoexPriceToMoneyValue(price: String?): MoneyValue {
    if (price == null) {
        return MoneyValue()
    }
    var exchangeUnits = 10.0.pow(countDigitsAfterPoint(price).toDouble()).toInt()
    var value = (price.toDouble() * exchangeUnits).toLong()
    if (exchangeUnits < 100) {
        exchangeUnits *= 10
        value *= 10
    }
    return MoneyValue(value, exchangeUnits, "RUR")
}

fun countDigitsAfterPoint(number: String): Int = if (number.indexOf('.') != -1) {
    number.reversed().chars().takeWhile { it.toChar() != '.' }.count().toInt()
} else {
    1
}