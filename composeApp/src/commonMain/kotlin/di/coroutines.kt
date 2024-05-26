package di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class CoroutineDispatchers {
    IO, MAIN
}

val coroutines = module {
    single(named(CoroutineDispatchers.IO)) { Dispatchers.IO + SupervisorJob() }

    single(named(CoroutineDispatchers.MAIN)) { Dispatchers.Main + SupervisorJob() }
}