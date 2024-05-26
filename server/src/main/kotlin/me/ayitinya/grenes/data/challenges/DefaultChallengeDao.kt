package me.ayitinya.grenes.data.challenges

import io.ktor.util.logging.*
import kotlinx.datetime.Instant
import me.ayitinya.grenes.data.Db
import me.ayitinya.grenes.data.users.UserId
import me.ayitinya.grenes.data.users.UsersTable
import me.ayitinya.grenes.data.users.toUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

private val LOGGER = KtorSimpleLogger("ChallengeDao")

class DefaultChallengeDao : ChallengeDao {
    override suspend fun create(
        title: String,
        description: String,
        suggestedBy: String?,
        challengeTypes: List<UUID>,
        startDate: Instant?,
        endDate: Instant?,
    ): Challenge? {
        return Db.query {

            val insertStatement = Challenges.insert {
                it[Challenges.title] = title
                it[Challenges.description] = description
                it[Challenges.suggestedBy] = suggestedBy
                it[Challenges.startDate] = startDate
                it[Challenges.endDate] = endDate
            }

            if (insertStatement.resultedValues.isNullOrEmpty()) {
                return@query null
            }


            val user = suggestedBy?.let {
                UsersTable.select { UsersTable.uid eq (suggestedBy) }.single().toUser()
            }

            challengeTypes.forEach { challengeType ->
                ChallengeTypeChallenges.insert {
                    it[ChallengeTypeChallenges.challengeType] = challengeType
                    it[challenge] = insertStatement.resultedValues!!.first()[Challenges.id]
                }
            }

            return@query insertStatement.resultedValues!!.first().let {
                return@let it.toChallenge(suggestedBy = user, challengeTypes = challengeTypes.map { challengeType ->
                    ChallengeTypes.select { ChallengeTypes.id eq challengeType }.single().toChallengeType()
                })
            }

        }
    }

    override suspend fun createChallengeType(name: String): ChallengeType? {
        return Db.query {
            val insertStatement = ChallengeTypes.insert {
                it[ChallengeTypes.name] = name
            }
            if (insertStatement.insertedCount <= 0) {
                return@query null
            }

            return@query insertStatement.resultedValues?.first()?.toChallengeType()
        }
    }

    override suspend fun getAllChallengeTypes(): List<ChallengeType> = Db.query {
        ChallengeTypes.selectAll().map { it.toChallengeType() }
    }

    override suspend fun deleteChallengeType(uid: UUID): Int = Db.query {
        ChallengeTypes.deleteWhere { ChallengeTypes.id eq uid }
    }

    override suspend fun read(uid: UUID): Challenge? {
        return Db.query {
            return@query Challenges.select { Challenges.id eq uid }.singleOrNull()?.let {
                val challengeTypes = ChallengeTypeChallenges.select { ChallengeTypeChallenges.challenge eq uid }.map {
                    ChallengeTypes.select { ChallengeTypes.id eq it[ChallengeTypeChallenges.challengeType] }.single()
                        .toChallengeType()
                }

                return@let it.toChallenge(challengeTypes = challengeTypes)
            }
        }
    }

    override suspend fun read(
        userId: UserId?,
        challengeType: List<String>?,
        date: Instant?,
        suggestedBy: String?,
        isActive: Boolean?,
    ): List<Challenge> = Db.query {

        val query = Challenges.selectAll()

        date?.let { query.andWhere { Challenges.startDate lessEq it and (Challenges.endDate greaterEq it) } }

        isActive?.let { query.andWhere { Challenges.isActive eq it } }

        suggestedBy?.let { query.andWhere { Challenges.suggestedBy eq suggestedBy } }

        return@query query.map { row ->
            val suggested =
                if (row[Challenges.suggestedBy] != null) UsersTable.select { UsersTable.uid eq row[Challenges.suggestedBy]!! }
                    .singleOrNull()?.toUser() else null

            val challengeTypes = ChallengeTypeChallenges.leftJoin(
                ChallengeTypes,
                { ChallengeTypeChallenges.challengeType },
                { ChallengeTypes.id })
                .select { ChallengeTypeChallenges.challenge eq row[Challenges.id] }
                .map { it.toChallengeType() }

            row.toChallenge(suggested, challengeTypes)
        }
    }

    override suspend fun update(uid: UUID, challenge: Challenge): Int = try {
        Db.query {
            ChallengeTypeChallenges.deleteWhere { ChallengeTypeChallenges.challenge eq uid }

            ChallengeTypeChallenges.batchInsert(data = challenge.challengeTypes, ignore = true) {
                this[ChallengeTypeChallenges.challengeType] = UUID.fromString(it.uid)
                this[ChallengeTypeChallenges.challenge] = uid
            }

            Challenges.update({ Challenges.id eq uid }) {
                it[title] = challenge.title
                it[description] = challenge.description
                it[startDate] = challenge.startAt
                it[endDate] = challenge.endAt
                it[isActive] = challenge.isActive
                it[isTrackable] = challenge.isTrackable
                it[difficulty] = challenge.difficulty
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        LOGGER.error("${e.message}")
        0
    }

    override suspend fun delete(uid: UUID): Int = Db.query {
        Challenges.deleteWhere { Challenges.id eq uid }
    }
}
