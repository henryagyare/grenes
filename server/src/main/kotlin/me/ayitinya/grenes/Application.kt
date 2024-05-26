package me.ayitinya.grenes

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.sentry.Sentry
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.di.appModule
import me.ayitinya.grenes.di.initializeDbModule
import me.ayitinya.grenes.plugins.configureAuthentication
import me.ayitinya.grenes.plugins.configureRequestValidation
import me.ayitinya.grenes.plugins.configureRouting
import me.ayitinya.grenes.plugins.configureSerialization
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.di() {
    val driverClassName = environment.config.property("ktor.db.driver").getString()
    val jdbcURL = environment.config.property("ktor.db.url").getString()
    val userName = environment.config.propertyOrNull("ktor.db.user")?.getString()
    val password = environment.config.propertyOrNull("ktor.db.password")?.getString()
    val datasourceProperties = environment.config.propertyOrNull("ktor.db.datasourceProperties")
        ?.getList()
        ?.associate { it.split(":", limit = 2).let { (k, v) -> k to v } }
    val isProduction = environment.config.propertyOrNull("ktor.development")?.getString() == "false"

    println("dbUser: $userName password: $password")

    install(Koin) {
        slf4jLogger()

        modules(
            appModule(isProduction = isProduction),
            initializeDbModule(
                driverClassName = driverClassName,
                jdbcURL = jdbcURL,
                userName = userName,
                password = password,
                dataSourceProperties = datasourceProperties ?: emptyMap()
            )
        )
    }
}

fun Application.configureDb() {
    val db: Db by inject()
    TransactionManager.defaultDatabase = db.database
}


fun Application.main(testing: Boolean = false) {
    install(Resources) // install before routing

    if (!testing) {
        di()
        configureDb()

        Sentry.init { options ->
            options.dsn =
                "https://3ca0752869f44d5aba59b065c69fb597@o1104921.ingest.sentry.io/4505248530563072"

            // Set traces_sample_rate to 1.0 to capture 100%
            // of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.tracesSampleRate = 1.0
        }
    }

    configureAuthentication() // install before routing

    configureRouting()
    configureSerialization()

    configureRequestValidation()

    install(IgnoreTrailingSlash)
    install(AutoHeadResponse)
}
