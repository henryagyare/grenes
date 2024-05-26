package di

import org.koin.core.module.Module
import org.koin.dsl.module

//val appModules = module {
//    single(createdAtStart = true) {
//        dataStorePreferences(
//            corruptionHandler = null,
//            migrations = emptyList(),
//            coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
//        )
//    }
//}

val androidModules = module {
}


actual val platformModules: List<Module> = listOf(androidModules)