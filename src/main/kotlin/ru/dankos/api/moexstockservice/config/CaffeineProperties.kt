package ru.dankos.api.moexstockservice.config


import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "caching")
class CaffeineProperties(
    val cacheNames: Map<String, CacheSpec>
)

class CacheSpec(
    val timeout: Long,
)