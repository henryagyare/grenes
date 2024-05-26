package me.ayitinya.grenes.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import me.ayitinya.grenes.data.challenges.ChallengeTypeChallenges
import me.ayitinya.grenes.data.challenges.ChallengeTypes
import me.ayitinya.grenes.data.challenges.Challenges
import me.ayitinya.grenes.data.feed.FeedComments
import me.ayitinya.grenes.data.feed.FeedsTable
import me.ayitinya.grenes.data.feed.ReactionsTable
import me.ayitinya.grenes.data.media.MediaTable
import me.ayitinya.grenes.data.users.UsersTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

internal class Db(
    driverClassName: String,
    jdbcURL: String,
    userName: String? = null,
    password: String? = null,
    dataSourceProperties: Map<String, String> = emptyMap(),
) {
    private val dataSource: HikariDataSource = try {
        createHikariDataSource(
            url = jdbcURL,
            driver = driverClassName,
            username = userName,
            password = password,
            dataSourceProperties = dataSourceProperties
        )
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }

    val database: Database = Database.connect(dataSource)

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                MediaTable,
                ChallengeTypes,
                Challenges,
                ChallengeTypeChallenges,
                FeedsTable,
                FeedComments,
                ReactionsTable,
            )
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        username: String? = null,
        password: String? = null,
        dataSourceProperties: Map<String, String> = emptyMap(),
    ) = HikariDataSource(HikariConfig().apply {
        this.username = username
        this.password = password
        dataSourceProperties.forEach { (key, value) -> addDataSourceProperty(key, value) }
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    companion object {
        suspend fun <T> query(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}