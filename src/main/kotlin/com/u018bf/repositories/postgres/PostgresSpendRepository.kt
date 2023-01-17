package com.u018bf.repositories.postgres

import com.u018bf.domain.models.Issue
import com.u018bf.domain.models.Spend
import com.u018bf.domain.repositories.ISpendRepository
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.duration
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class PostgresSpendRepository(private val database: Database) : ISpendRepository {
    object Spends : UUIDTable("spends") {
        val duration = duration("duration")
        val userID = varchar("user_id", 255)
        val issueID = reference("issue_id", PostgresIssueRepository.Issues, onDelete = ReferenceOption.CASCADE)
        val date = date("date")
    }

    class SpendEntity(id: EntityID<UUID>) : UUIDEntity(id) {
        companion object : UUIDEntityClass<SpendEntity>(Spends)

        var duration by Spends.duration
        var userID by Spends.userID
        var issueID by Spends.issueID
        var issue by PostgresIssueRepository.IssueEntity referencedOn Spends.issueID
        var date by Spends.date

        fun toModel() = Spend("", duration.toKotlinDuration(), userID, issueID.toString(), date.toKotlinLocalDate())
    }

    private fun resultRowToSpend(row: ResultRow) = Spend(
        id = row[Spends.id].toString(),
        duration = row[Spends.duration].toKotlinDuration(),
        userID = row[Spends.userID],
        issueID = row[Spends.issueID].toString(),
        date = row[Spends.date].toKotlinLocalDate()
    )

    override fun save(spends: List<Spend>) {
        val result = transaction(database) {
            Spends.batchInsert(spends) {
                this[Spends.duration] = it.duration.toJavaDuration()
                this[Spends.userID] = it.userID
                this[Spends.issueID] = it.issueID
                this[Spends.date] = it.date.toJavaLocalDate()
            }
        }
        result.map(::resultRowToSpend)
    }

    fun ResultRow.toSpend(): Spend = Spend(
        id = this[Spends.id].toString(),
        duration = this[Spends.duration].toKotlinDuration(),
        userID = this[Spends.userID],
        issueID = this[Spends.issueID].toString(),
        date = this[Spends.date].toKotlinLocalDate(),
    )
}