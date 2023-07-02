package com.`018bf`.interfaces.space.commands

import com.`018bf`.domain.usecases.IReportUseCase
import com.`018bf`.domain.models.MonthlyReport
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

class MonthlyReportCommand(private val reportUseCase: IReportUseCase, override val spaceClient: SpaceClient) : ICommand {
    override val name = "monthly"
    override val info = "Show monthly time tracking report"

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
            val report = reportUseCase.getMonthlyReportByUser(payload.userId, args.date)
            sendMessage(payload.userId, reportMessage(report))
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

    private fun reportMessage(report: MonthlyReport): ChatMessage {
        val style = MessageStyle.SUCCESS
        return message {
            section {
                text(
                    "${report.date} ${report.date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}",
                    style
                )
                fields {
                    report.spend.forEach {
                        field(it.title.take(60), it.spend.toString())
                    }
                    field("Total", report.getTotal().toString())
                }
            }
        }
    }

    private fun getArgs(payload: MessagePayload): MonthlyReportArgs? {
        val today = Calendar.getInstance().time.toInstant().toKotlinInstant().toLocalDateTime(TimeZone.UTC).date
        val args = payload.commandArguments()?.replace("\n", "") ?: return null
        return try {
            val date =
                args.substringBefore(" ").takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) } ?: today
            MonthlyReportArgs(date = date)
        } catch (e: Exception) {
            return null
        }
    }
}