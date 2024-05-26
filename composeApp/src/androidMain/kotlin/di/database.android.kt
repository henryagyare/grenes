package di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import applicationContext
import me.ayitinya.grenes.Database
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

actual fun Module.databaseDriverFactory(): KoinDefinition<SqlDriver> = single {
    AndroidSqliteDriver(Database.Schema, context = applicationContext, name = "test.db")
}