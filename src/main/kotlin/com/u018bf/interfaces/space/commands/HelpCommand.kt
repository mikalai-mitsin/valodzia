package com.u018bf.interfaces.space.commands

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*

class HelpCommand(override val spaceClient: SpaceClient) : ICommand {
    override val name = "help"
    override val info = "Show this help"

    override suspend fun run(payload: MessagePayload) {
        spaceClient.chats.messages.sendMessage(
            channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(payload.userId)),
            helpMessage()
        )
    }

    private fun helpMessage(): ChatMessage {
        return message {
            MessageOutline(
                icon = ApiIcon("checkbox-checked"),
                text = "valodzia bot help"
            )
            section {
                text("List of available commands", MessageStyle.PRIMARY)
                fields {
                    field(name, info)
                    field("report", "show time tracking report")
                    field("rebuild", "rebuild reports")
                }
            }
        }
    }
}