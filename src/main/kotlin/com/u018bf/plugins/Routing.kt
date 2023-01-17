package com.u018bf.plugins

import com.u018bf.interfaces.space.MessageHandler
import com.u018bf.repositories.postgres.PostgresIssueRepository
import com.u018bf.repositories.postgres.PostgresSpendRepository
import com.u018bf.repositories.space.SpaceIssueRepository
import com.u018bf.repositories.space.WorkingDayRepository
import com.u018bf.usecases.IssueUseCase
import com.u018bf.usecases.ReportUseCase
import io.ktor.client.plugins.cache.*

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace


fun Application.configureRouting() {
    val clientId = environment.config.property("ktor.space.clientId").getString()
    val clientSecret = environment.config.property("ktor.space.clientSecret").getString()
    val spaceServerUrl = environment.config.property("ktor.space.spaceServerUrl").getString()
    val spaceHttpClient = ktorClientForSpace {
        install(HttpCache)
    }
    val spaceAppInstance = SpaceAppInstance(
        clientId = clientId,
        clientSecret = clientSecret,
        spaceServerUrl = spaceServerUrl
    )
    val space = SpaceClient(
        ktorClient = spaceHttpClient,
        appInstance = spaceAppInstance,
        auth = SpaceAuth.ClientCredentials()
    )
    val database = Database.connect(
        "jdbc:postgresql://localhost:5432/valodzia", driver = "org.postgresql.Driver",
        user = "mikalai", password = "met31415"
    )
    transaction(database) {
        SchemaUtils.create(PostgresIssueRepository.Issues)
        SchemaUtils.create(PostgresSpendRepository.Spends)
    }
    val spaceIssueRepository = SpaceIssueRepository(space)
    val postgresIssueRepository = PostgresIssueRepository(database)
    val postgresSpendRepository = PostgresSpendRepository(database)
    val workingDayRepository = WorkingDayRepository(space)
    val reportUserCase = ReportUseCase(postgresIssueRepository, workingDayRepository)
    val issueUseCase = IssueUseCase(spaceIssueRepository, postgresIssueRepository, postgresSpendRepository)
    val messageHandler = MessageHandler(reportUserCase, issueUseCase, space)
    routing {
        post("api/space") {
            messageHandler.handle(call)
        }
        get("report") {
            val reports = reportUserCase.listDailyReportByUserAndDates(
                LocalDate.parse("2023-01-01"),
                LocalDate.parse("2023-01-05"),
                "a25ro0qsUBl",
            )
            call.respondText(reports.toString())
        }
        get("sync") {
            issueUseCase.sync()
        }
        get("/") {
            call.respondText("Žyvie")
        }
    }
}
