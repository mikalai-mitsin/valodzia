package com.u018bf.interfaces.space.commands

import com.u018bf.domain.usecases.IIssueUseCase
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*

class RebuildCommand(private val issueUseCase: IIssueUseCase, override val spaceClient: SpaceClient) : ICommand {
    override val name = "rebuild"
    override val info = "Rebuild reports"

    override suspend fun run(payload: MessagePayload) {
        spaceClient.chats.messages.sendMessage(
            channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(payload.userId)),
            start()
        )
        issueUseCase.sync()
        spaceClient.chats.messages.sendMessage(
            channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(payload.userId)),
            done()
        )
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

    private fun done(): ChatMessage {
        return message {
            section {
                text(
                    "Done!",
                    MessageStyle.PRIMARY
                )
            }
        }
    }
}