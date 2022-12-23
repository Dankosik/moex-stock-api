package ru.dankos.api.moexstockservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "moex")
class MoexProperties(
    val api: Api,
)

class Api(
    val shares: Shares
)

class Shares(
    val defaultCurrency: String
)