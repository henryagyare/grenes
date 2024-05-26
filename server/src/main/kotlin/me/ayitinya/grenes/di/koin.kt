package me.ayitinya.grenes.di

import me.ayitinya.grenes.config.firebase.FirebaseAdmin
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.challenges.ChallengeDao
import me.ayitinya.grenes.data.challenges.DefaultChallengeDao
import me.ayitinya.grenes.data.feed.DefaultFeedDao
import me.ayitinya.grenes.data.feed.DefaultFeedService
import me.ayitinya.grenes.data.feed.FeedDao
import me.ayitinya.grenes.data.feed.FeedService
import me.ayitinya.grenes.data.media.DefaultMediaDao
import me.ayitinya.grenes.data.media.DefaultMediaService
import me.ayitinya.grenes.data.media.MediaDao
import me.ayitinya.grenes.data.media.MediaService
import me.ayitinya.grenes.data.users.DefaultUserDao
import me.ayitinya.grenes.data.users.UserDao
import me.ayitinya.grenes.storage.FirebaseStorage
import me.ayitinya.grenes.storage.MockStorage
import me.ayitinya.grenes.storage.Storage
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initializeDbModule(
    driverClassName: String,
    jdbcURL: String,
    userName: String? = null,
    password: String? = null,
    dataSourceProperties: Map<String, String> = emptyMap(),
): Module {
    return module {
        single {
            Db(
                driverClassName = driverClassName,
                jdbcURL = jdbcURL,
                userName = userName,
                password = password,
                dataSourceProperties = dataSourceProperties
            )
        }

        factory(qualifier = named("test")) { params ->
            Db(
                driverClassName = "org.h2.Driver",
                jdbcURL = "jdbc:h2:mem:${params.get<String>()};DB_CLOSE_DELAY=-1"
            )
        }
    }
}

val dbModule = module {

}

fun appModule(isProduction: Boolean = true) = module {
    single<UserDao> { DefaultUserDao() }

    single(createdAtStart = true) {
        FirebaseAdmin()
    }

    single<MediaDao> { DefaultMediaDao() }

    single<ChallengeDao> { DefaultChallengeDao() }

    single<FeedDao> { DefaultFeedDao(challengeDao = get()) }

    single<FeedService> {
        DefaultFeedService(
            mediaService = get(),
            feedDao = get(),
            usersDao = get()
        )
    }

    single<MediaDao> { DefaultMediaDao() }

    single<Storage> { FirebaseStorage() }

    single<Storage>(named("mockStorage")) { MockStorage() }

    single<MediaService> {
        DefaultMediaService(
            storage = if (isProduction) get() else get(named("mockStorage")),
            mediaDao = get()
        )
    }
}
