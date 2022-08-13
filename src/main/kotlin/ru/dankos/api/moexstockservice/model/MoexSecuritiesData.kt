package ru.dankos.api.moexstockservice.model

import ru.dankos.api.moexstockservice.controller.dto.MoneyValue
import java.time.LocalTime

class MoexSecuritiesData(
    val ticker: String,
    val companyShortName: String,
    val companyFullName: String,
    val stockClosedPrice: MoneyValue,
)