package di

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import me.ayitinya.grenes.Database
import me.ayitinya.grenes.UserTable
import me.ayitinya.grenes.data.users.UserId
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module


// this could have just been a function that returns SqlDriver
// don't know why I did it this way
expect fun Module.databaseDriverFactory(): KoinDefinition<SqlDriver>

val databaseModule = module {
    databaseDriverFactory()

    single<Database> {
        val driver: SqlDriver = get()

        val userIdAdapter = object : ColumnAdapter<UserId, String> {
            override fun decode(databaseValue: String) = UserId(databaseValue)
            override fun encode(value: UserId) = value.value
        }

        val createdAtAdapter = object : ColumnAdapter<Instant, String> {
            override fun decode(databaseValue: String) = Instant.parse(databaseValue)
            override fun encode(value: Instant) = value.toString()

        }

        Database(driver, UserTableAdapter = UserTable.Adapter(userIdAdapter, createdAtAdapter))
    }
}