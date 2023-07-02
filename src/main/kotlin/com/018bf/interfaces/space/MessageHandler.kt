package com.`018bf`.interfaces.space

import com.fasterxml.jackson.databind.ObjectMapper
import com.`018bf`.interfaces.space.commands.HelpCommand
import com.`018bf`.interfaces.space.commands.DailyReportCommand
import com.`018bf`.interfaces.space.commands.MonthlyReportCommand
import com.`018bf`.usecases.ReportUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.*
import space.jetbrains.api.runtime.types.*


class MessageHandler(reportUseCase: ReportUseCase, space: SpaceClient) {
    private val supportedCommands = listOf(
        HelpCommand(space),
        DailyReportCommand(reportUseCase, space),
        MonthlyReportCommand(reportUseCase, space),
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