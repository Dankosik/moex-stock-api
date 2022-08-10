package ru.dankos.api.moexstockservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    properties = ["spring.cloud.discovery.enabled=false"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
class BaseIntegrationTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient
}