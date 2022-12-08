package com.mitsin.plugins

import com.mitsin.interfaces.space.MessageHandler
import com.mitsin.repositories.IssueRepository
import com.mitsin.usecases.ReportUseCase
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace


fun Application.configureRouting() {
    val clientId = environment.config.property("ktor.space.clientId").getString()
    val clientSecret = environment.config.property("ktor.space.clientSecret").getString()
    val spaceServerUrl = environment.config.property("ktor.space.spaceServerUrl").getString()
    val spaceHttpClient = ktorClientForSpace()
    val spaceAppInstance = SpaceAppInstance(
        clientId = clientId,
        clientSecret = clientSecret,
        spaceServerUrl = spaceServerUrl
    )
    val space =
        SpaceClient(ktorClient = spaceHttpClient, appInstance = spaceAppInstance, auth = SpaceAuth.ClientCredentials())
    val issueRepository = IssueRepository(space)
    val reportUserCase = ReportUseCase(issueRepository)
    val messageHandler = MessageHandler(reportUserCase, space)


    routing {
        post("api/space") {
            messageHandler.handle(call)
        }
        get("/") {
            call.respond(HttpStatusCode.OK, "")
        }
    }
}
