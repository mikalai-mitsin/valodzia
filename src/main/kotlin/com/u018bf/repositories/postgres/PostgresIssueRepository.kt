package com.u018bf.repositories.postgres

import com.u018bf.domain.models.Issue
import com.u018bf.domain.models.Spend
import com.u018bf.domain.repositories.IIssueRepository
import com.u018bf.repositories.postgres.PostgresIssueRepository.IssueEntity.Companion.referrersOn
import com.u018bf.repositories.space.StringEntity
import com.u018bf.repositories.space.StringEntityClass
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.toKotlinDuration

class PostgresIssueRepository(private val database: Database) : IIssueRepository {
    object Issues : IdTable<String>() {
        override val id = varchar("id", 255).entityId()
        val title = varchar("title", 255)
        val project = varchar("project", 255)
        override val primaryKey = PrimaryKey(id)
    }

    class IssueEntity(id: EntityID<String>) : StringEntity(id) {
        companion object : StringEntityClass<IssueEntity>(Issues)

        private val title by Issues.title
        private val project by Issues.project

        val spends by PostgresSpendRepository.SpendEntity referrersOn PostgresSpendRepository.Spends.issueID
//        private val spends by PostgresSpendRepository.SpendEntity optionalReferrersOn PostgresSpendRepository.Spends.issueID

        fun toModel() = Issue("", title, project, spends.map { it.toModel() })
//            var i = Issue("", title, project, emptyList())
//            if (spends != null) {
//                spends.map { it.toModel() }
//            }
//            Issue("", title, project, emptyList())

    }

    override suspend fun list(user: String?, from: LocalDate?, to: LocalDate?): List<Issue> {
        val res = transaction(database) {

//            IssueEntity.find {
//                PostgresSpendRepository.Spends.userID eq user!!
//                PostgresSpendRepository.Spends.date.between(from!!.toJavaLocalDate(), to!!.toJavaLocalDate())
//            }.toList().map { it.toModel() }
            val q = Issues.rightJoin(PostgresSpendRepository.Spends).selectAll().andWhere {
                PostgresSpendRepository.Spends.userID eq user!! and
                        PostgresSpendRepository.Spends.date.between(from?.toJavaLocalDate(), to?.toJavaLocalDate())
            }.withDistinct()
            IssueEntity.wrapRows(q).toList().map { it.toModel() }
        }

        return res
    }

    override fun save(issues: List<Issue>) {
        transaction(database) {
            Issues.batchInsert(issues) {
                this[Issues.id] = it.id
                this[Issues.title] = it.title
                this[Issues.project] = it.id
            }
        }
    }

    override fun clean() {
        transaction(database) { Issues.deleteAll() }
    }

    private fun ResultRow.toSpend(): Spend = Spend(
        id = this[PostgresSpendRepository.Spends.id].toString(),
        duration = this[PostgresSpendRepository.Spends.duration].toKotlinDuration(),
        userID = this[PostgresSpendRepository.Spends.userID],
        issueID = this[PostgresSpendRepository.Spends.issueID].toString(),
        date = this[PostgresSpendRepository.Spends.date].toKotlinLocalDate(),
    )

    fun ResultRow.toIssue(): Issue = Issue(
        id = this[Issues.id].value,
        title = this[Issues.title],
        project = this[Issues.project],
        spends = this[Issues.id].value.let {
            PostgresSpendRepository.Spends.select { PostgresSpendRepository.Spends.issueID eq it }.map { it.toSpend() }
        }
    )
}
