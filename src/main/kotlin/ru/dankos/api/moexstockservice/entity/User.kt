package ru.dankos.api.moexstockservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.dankos.api.moexstockservice.controller.dto.MoneyValue

@Document
data class User(
    @Id
    val id: String? = null,
    var subscriptions: MutableList<Subscription>,
)

data class Subscription(
    val ticker: String,
    val price: MoneyValue
)