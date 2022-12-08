package com.`018bf`.interfaces.space.commands

import com.`018bf`.domain.models.DailyReport
import com.`018bf`.domain.usecases.IReportUseCase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
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
        val reports =
            reportUseCase.listDailyReportByUserAndDates(args.from, args.to, payload.userId)
        reports.forEach {
            sendMessage(payload.userId, reportMessage(it))
        }
    }

    private fun reportMessage(report: DailyReport): ChatMessage {
        val style = when (report.getTotal().inWholeHours) {
            in 8..9 -> MessageStyle.SUCCESS
            in 6..7 -> MessageStyle.WARNING
            else -> MessageStyle.ERROR
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
        val args = payload.commandArguments() ?: return null
        return try {
            val start =
                args.substringBefore(" ").takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) } ?: today
            val end = args.substringAfter(" ").trimStart().takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
                ?: today
            ReportArgs(from = start, to = end)
        } catch (e: Exception) {
            null
        }
    }
}