package di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import me.ayitinya.grenes.Database
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

actual fun Module.databaseDriverFactory(): KoinDefinition<SqlDriver> = single {
    NativeSqliteDriver(Database.Schema, "test.db")
}