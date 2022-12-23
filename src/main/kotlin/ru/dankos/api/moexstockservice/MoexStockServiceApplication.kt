package ru.dankos.api.moexstockservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import ru.dankos.api.moexstockservice.config.CaffeineProperties
import ru.dankos.api.moexstockservice.config.MoexProperties

@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties(
    MoexProperties::class,
    CaffeineProperties::class
)
class MoexStockServiceApplication

fun main(args: Array<String>) {
    runApplication<MoexStockServiceApplication>(*args)
}
