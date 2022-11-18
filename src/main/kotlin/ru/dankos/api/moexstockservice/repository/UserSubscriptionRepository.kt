package ru.dankos.api.moexstockservice.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import ru.dankos.api.moexstockservice.entity.User

@Repository
interface UserSubscriptionRepository : ReactiveMongoRepository<User, String>