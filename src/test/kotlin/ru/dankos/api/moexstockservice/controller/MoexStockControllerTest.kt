package ru.dankos.api.moexstockservice.controller

class MoexStockControllerTest : BaseIntegrationTest() {

//    @Test
//    fun `test get moex stock by ticker`() {
//        stubForStockFromMoex("SBER", "src/test/resources/__files/moex-response/sber-stock-price.json")
//        webTestClient.get()
//            .uri("/stocks/SBER/price")
//            .exchange()
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectStatus().is2xxSuccessful
//            .expectBody()
//            .json(File("src/test/resources/__files/expected-response/sber-stock-price.json").readText(Charsets.UTF_8))
//    }
//
//    @Test
//    fun `test get moex fees by ticker`() {
//        stubForStockFromMoex("FEES", "src/test/resources/__files/moex-response/fees-stock-price.json")
//        webTestClient.get()
//            .uri("/stocks/FEES/price")
//            .exchange()
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectStatus().is2xxSuccessful
//            .expectBody()
//            .json(File("src/test/resources/__files/expected-response/fees-stock-price.json").readText(Charsets.UTF_8))
//    }
//
//    @Test
//    fun `test get moex tatn by ticker`() {
//        stubForStockFromMoex("TATN", "src/test/resources/__files/moex-response/tatn-stock-price.json")
//        webTestClient.get()
//            .uri("/stocks/TATN/price")
//            .exchange()
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectStatus().is2xxSuccessful
//            .expectBody()
//            .json(File("src/test/resources/__files/expected-response/tatn-stock-price.json").readText(Charsets.UTF_8))
//    }
//
//    @Test
//    fun `test get moex vtbr by ticker`() {
//        stubForStockFromMoex("VTBR", "src/test/resources/__files/moex-response/vtbr-stock-price.json")
//        webTestClient.get()
//            .uri("/stocks/VTBR/price")
//            .exchange()
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectStatus().is2xxSuccessful
//            .expectBody()
//            .json(File("src/test/resources/__files/expected-response/vtbr-stock-price.json").readText(Charsets.UTF_8))
//    }
//
//    @Test
//    fun `test get moex yndx by ticker`() {
//        stubForStockFromMoex("YNDX", "src/test/resources/__files/moex-response/yndx-stock-price.json")
//        webTestClient.get()
//            .uri("/stocks/YNDX/price")
//            .exchange()
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectStatus().is2xxSuccessful
//            .expectBody()
//            .json(File("src/test/resources/__files/expected-response/yndx-stock-price.json").readText(Charsets.UTF_8))
//    }
}