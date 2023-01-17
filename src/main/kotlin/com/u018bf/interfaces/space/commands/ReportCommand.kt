package com.u018bf.interfaces.space.commands

import com.u018bf.domain.models.DailyReport
import com.u018bf.domain.usecases.IReportUseCase
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.commandArguments
import space.jetbrains.api.runtime.types.MessagePayload
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessageStyle
import java.time.format.TextStyle
import java.util.*

class ReportCommand(private val reportUseCase: IReportUseCase, override val spaceClient: SpaceClient) : ICommand {
    override val name = "report"
    override val info = "Show time tracking report"

    override suspend fun run(payload: MessagePayload) {
        val args = getArgs(payload) ?: run {
            sendMessage(payload.userId, message {
                section {
                    text("Invalid args", MessageStyle.PRIMARY)
                }
            })
            return
        }
        sendMessage(userID = payload.userId, start())
        try {
            val reports =
                reportUseCase.listDailyReportByUserAndDates(args.from, args.to, payload.userId)
            reports.forEach {
                sendMessage(payload.userId, reportMessage(it))
            }
        } catch (e: Exception) {
            sendMessage(payload.userId, errorMessage(e))
        }
    }

    private fun errorMessage(e: Exception): ChatMessage {
        return message {
            section {
                text(
                    "Chieravascieńka mnie niešta \uD83D\uDE15  \n $e",
                    MessageStyle.ERROR
                )
            }
        }
    }

    private fun start(): ChatMessage {
        return message {
            section {
                text(
                    "Let me see \uD83D\uDC40",
                    MessageStyle.PRIMARY
                )
            }
        }
    }

    private fun reportMessage(report: DailyReport): ChatMessage {
        val style = when (report.getLevel()) {
            DailyReport.Level.OK ->
                MessageStyle.SUCCESS
            DailyReport.Level.WARNING ->
                MessageStyle.WARNING
            else ->
                MessageStyle.ERROR
        }
        return message {
            section {
                text(
                    "${report.date} ${report.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}",
                    style
                )
                fields {
                    report.spend.forEach {
                        field(it.title, it.spend.toString())
                    }
                    field("Total", report.getTotal().toString())
                }
            }
        }
    }

    private fun getArgs(payload: MessagePayload): ReportArgs? {
        val today = Calendar.getInstance().time.toInstant().toKotlinInstant().toLocalDateTime(TimeZone.UTC).date
        val args = payload.commandArguments()?.replace("\n", "") ?: return null
        return try {
            val start =
                args.substringBefore(" ").takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) } ?: today
            val end = args.substringAfter(" ").trimStart().takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
                ?: today
            ReportArgs(from = start, to = end)
        } catch (e: Exception) {
            return null
        }
    }
}