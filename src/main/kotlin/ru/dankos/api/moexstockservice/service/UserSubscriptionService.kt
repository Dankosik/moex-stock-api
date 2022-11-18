package ru.dankos.api.moexstockservice.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import ru.dankos.api.moexstockservice.entity.Subscription
import ru.dankos.api.moexstockservice.entity.User
import ru.dankos.api.moexstockservice.repository.UserSubscriptionRepository

@Service
class UserSubscriptionService(
    private val userSubscriptionRepository: UserSubscriptionRepository,
    private val mongoTemplate: ReactiveMongoTemplate
) {

    suspend fun a(
        ticker: String,
        priceFrom: Long,
        priceTo: Long
    ): Map<String, List<Subscription>> {
        val query = if (priceFrom > priceTo) {
            Query(Criteria.where("subscriptions.price.value").lt(priceFrom).gt(priceTo))
                .addCriteria(Criteria.where("subscriptions.ticker").`is`(ticker))
        } else {
            Query(Criteria.where("subscriptions.price.value").lt(priceTo).gt(priceFrom))
                .addCriteria(Criteria.where("subscriptions.ticker").`is`(ticker))
        }
        return mongoTemplate.find(query, User::class.java).collectList().awaitSingle()
            .associateBy(
                { user -> user.id!! },
                { user -> user.subscriptions.filter { digitInRange(priceFrom, priceTo, it.price.value!!) } }
            )
    }

    suspend fun sendNotificationThatPriceReachedSubscription(
        ticker: String,
        priceFrom: Long,
        priceTo: Long
    ) {
        println("priceFrom: $priceFrom")
        println("priceTo: $priceTo")

        val query = if (priceFrom > priceTo) {
            Query(Criteria.where("subscriptions.price.value").lt(priceFrom).gt(priceTo))
                .addCriteria(Criteria.where("subscriptions.ticker").`is`(ticker))
        } else {
            Query(Criteria.where("subscriptions.price.value").lt(priceTo).gt(priceFrom))
                .addCriteria(Criteria.where("subscriptions.ticker").`is`(ticker))
        }

        val userIdToSubscriptions = mongoTemplate.find(query, User::class.java).collectList().awaitSingle()
            .associateBy(
                { user -> user.id!! },
                { user -> user.subscriptions.filter { digitInRange(priceFrom, priceTo, it.price.value!!) } }
            )

        deleteExecutedSubscriptions(userIdToSubscriptions)

        sendToKafka(userIdToSubscriptions)
    }

    private suspend fun deleteExecutedSubscriptions(userIdToSubscriptions: Map<String, List<Subscription>>) {
        userIdToSubscriptions.entries.forEach { map ->
            val user = userSubscriptionRepository.findById(map.key).awaitSingle()
            user.subscriptions.removeAll(map.value)
            userSubscriptionRepository.save(user).awaitSingle()
        }
    }

    private fun sendToKafka(userMap: Map<String, List<Subscription>>) {
        userMap.entries.forEach {
            val resultMessage = "Юзеру с id: ${it.key} отправляю месседж "
            it.value.forEach { subscription ->
                val s =
                    resultMessage + "Цена достигла ${subscription.price.value!!.toDouble() / 100} для ${subscription.ticker}"
                println(s)
            }
        }

    }

    private fun digitInRange(from: Long, to: Long, digit: Long): Boolean = digit in from..to || digit in from downTo to
}