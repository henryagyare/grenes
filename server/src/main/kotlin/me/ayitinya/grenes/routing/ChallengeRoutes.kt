package me.ayitinya.grenes.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.datetime.Instant
import me.ayitinya.grenes.auth.firebase.FIREBASE_AUTH
import me.ayitinya.grenes.data.challenges.Challenge
import me.ayitinya.grenes.data.challenges.ChallengeCreation
import me.ayitinya.grenes.data.challenges.ChallengeDao
import org.koin.ktor.ext.inject
import java.util.*

internal val LOGGER = KtorSimpleLogger("ChallengeRoutes")

fun Route.challengeRoutes() {
    val challengeDao by inject<ChallengeDao>()

//    authenticate(FIREBASE_AUTH) {
    get<ChallengeResource> {
        try {
            val startDate = it.date?.let { date ->
                try {
                    Instant.parse(date)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid date format")
                }
            }

            val challenges = challengeDao.read(
                challengeType = it.challengeType,
                date = startDate,
                suggestedBy = it.suggestedBy,
                isActive = it.isActive
            )

            call.respond(challenges)

        } catch (e: Exception) {
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        }
    }

    post<ChallengeResource> {
        try {
            val challenge = call.receive<ChallengeCreation>()
            val challengeTypes = challenge.challengeTypes.map { challengeType ->
                UUID.fromString(challengeType.uid)
            }

            val startAt = challenge.startAt?.let {
                try {
                    Instant.parse(it)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid date format")
                }
            }

            val endAt = challenge.endAt?.let {
                try {
                    Instant.parse(it)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid date format")
                }
            }

            val createdChallenge = challengeDao.create(
                title = challenge.title,
                description = challenge.description,
                suggestedBy = challenge.suggestedBy,
                challengeTypes = challengeTypes,
                startDate = startAt,
                endDate = endAt
            )
            if (createdChallenge != null) {
                call.respond(createdChallenge)
            } else {
                call.respondText(
                    "Challenge not created",
                    status = HttpStatusCode.InternalServerError
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()

            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get<ChallengeResource.Detail> {
        try {
            val uid = UUID.fromString(it.uid)
            val challenge = challengeDao.read(uid)
            if (challenge != null) {
                call.respond(challenge)
            } else {
                call.respondText("No challenge found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    put<ChallengeResource.Detail> {
        try {
            val uid = UUID.fromString(it.uid)
            val challenge = call.receive<Challenge>()
            val updatedChallenge = challengeDao.update(uid = uid, challenge = challenge)

            if (updatedChallenge == 0) {
                call.respondText("No challenge found", status = HttpStatusCode.NotFound)
                return@put
            }

            call.respond(HttpStatusCode.OK)

        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    delete<ChallengeResource.Detail> {
        try {
            val uid = UUID.fromString(it.uid)
            challengeDao.delete(uid = uid)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    get<ChallengeResource.Types> {
        try {
            val challengeTypes = challengeDao.getAllChallengeTypes()
            call.respond(challengeTypes)
        } catch (e: Exception) {
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    post<ChallengeResource.Types> {
        try {
            val challengeType = call.receive<String>()
            val createdChallengeType = challengeDao.createChallengeType(challengeType)
            if (createdChallengeType != null) {
                call.respond(createdChallengeType)
            } else {
                call.respondText(
                    "Challenge type not created",
                    status = HttpStatusCode.InternalServerError
                )
            }
        } catch (e: Exception) {
            LOGGER.error("${e.message}")
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        }
    }
//    }
}