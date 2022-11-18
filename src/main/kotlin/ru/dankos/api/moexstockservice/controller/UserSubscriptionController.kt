package ru.dankos.api.moexstockservice.controller

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dankos.api.moexstockservice.entity.Subscription
import ru.dankos.api.moexstockservice.entity.User
import ru.dankos.api.moexstockservice.repository.UserSubscriptionRepository

@RestController
@RequestMapping("/users")
class UserSubscriptionController(
    private val userSubscriptionRepository: UserSubscriptionRepository
) {

    @GetMapping(value = ["/{userId}/subscriptions/{ticker}"])
    suspend fun getSubscriptionsForUserByTicker(
        @PathVariable userId: String,
        @PathVariable ticker: String
    ): List<Subscription> {
        val user = userSubscriptionRepository.findById(userId).awaitSingleOrNull()
            ?: throw RuntimeException("User not found with id: $userId")
        return user.subscriptions.filter { it.ticker == ticker }
    }

    @PostMapping(value = ["/{userId}/subscriptions"])
    suspend fun addSubscription(
        @RequestBody subscription: Subscription,
        @PathVariable userId: String
    ): User {
        val user = userSubscriptionRepository.findById(userId).awaitSingleOrNull()
        if (user == null) {
            val newUser = User(id = userId, subscriptions = mutableListOf(subscription))
            return userSubscriptionRepository.save(newUser).awaitSingle()
        }

        if (user.subscriptions.contains(subscription)) {
            throw RuntimeException("User have subscription: $subscription")
        }

        user.subscriptions.add(subscription)

        return userSubscriptionRepository.save(user).awaitSingle()
    }

    @DeleteMapping(value = ["/{userId}/subscriptions"])
    suspend fun deleteSubscription(
        @RequestBody subscription: Subscription,
        @PathVariable userId: String
    ): User {
        val user = userSubscriptionRepository.findById(userId).awaitSingleOrNull()
            ?: throw RuntimeException("User not found with id: $userId")
        user.subscriptions.remove(user.subscriptions.find { it == subscription })
        return userSubscriptionRepository.save(user).awaitSingle()
    }
}