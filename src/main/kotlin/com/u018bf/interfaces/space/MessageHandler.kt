package com.u018bf.interfaces.space

import com.fasterxml.jackson.databind.ObjectMapper
import com.u018bf.domain.usecases.IIssueUseCase
import com.u018bf.domain.usecases.IReportUseCase
import com.u018bf.interfaces.space.commands.HelpCommand
import com.u018bf.interfaces.space.commands.RebuildCommand
import com.u018bf.interfaces.space.commands.ReportCommand
import com.u018bf.usecases.ReportUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.*
import space.jetbrains.api.runtime.types.*


class MessageHandler(reportUseCase: IReportUseCase, issueUseCase: IIssueUseCase, space: SpaceClient) {
    private val supportedCommands = listOf(
        HelpCommand(space),
        ReportCommand(reportUseCase, space),
        RebuildCommand(issueUseCase, space),
    )

    private fun getSupportedCommands() = Commands(
        supportedCommands.map {
            it.toSpaceCommand()
        }
    )

    suspend fun handle(call: ApplicationCall) {
        val body = call.receiveText()
        when (val payload = readPayload(body)) {
            is ListCommandsPayload -> {
                call.respondText(
                    ObjectMapper().writeValueAsString(getSupportedCommands()).toString(),
                    ContentType.Application.Json
                )
            }

            is MessagePayload -> {
                // user sent a message to the application
                val commandName = payload.command()
                var command = supportedCommands.find { it.name == commandName }
                if (command == null) {
                    command = supportedCommands.find { it.name == "help" }
                } else {
                    GlobalScope.launch { command.run(payload) }
                }
                call.respond(HttpStatusCode.OK, "")
            }
        }
    }
}