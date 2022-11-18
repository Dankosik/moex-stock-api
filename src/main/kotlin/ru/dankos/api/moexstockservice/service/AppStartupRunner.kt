package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service

@Service
class AppStartupRunner(
    private val stocksService: StocksService
) : ApplicationRunner {


    override fun run(args: ApplicationArguments?): Unit = runBlocking {
        stocksService.pushNotificationWhenPriceIsEqualToSubscription()
    }
}