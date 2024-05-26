package di

import io.ktor.http.*
import org.koin.core.module.Module

fun commonModules(isProduction: Boolean = true): List<Module> {
    val url = if (isProduction) {
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = "grenes-1759f.uc.r.appspot.com"
        }
    } else {
        URLBuilder().apply {
            protocol = URLProtocol.HTTP
            port = 8080
            host = "10.0.2.2"
        }
    }
    return listOf(
        coroutines,
        viewModelModule,
        useCaseModule,
        appModule(isProduction),
        httpClientModule(url = url),
        databaseModule
    )
}

expect val platformModules: List<Module>
