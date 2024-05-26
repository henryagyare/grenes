package me.ayitinya.grenes.routing

import io.ktor.server.config.*
import io.ktor.server.testing.*
import me.ayitinya.grenes.main

fun ApplicationTestBuilder.init() {
    application {
        main(testing = true)
    }

    environment {
        config =
            ApplicationConfig("test_application.yaml").mergeWith(MapApplicationConfig("ktor.environment" to "test"))
    }
}