package ru.dankos.api.moexstockservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "moex")
class MoexProperties(
    val api: Api,
)

class Api(
    val shares: Shares
)

class Shares(
    val url: String,
    val defaultCurrency: String
)