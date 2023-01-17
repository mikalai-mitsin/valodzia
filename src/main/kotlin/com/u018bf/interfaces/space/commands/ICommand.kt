package com.u018bf.interfaces.space.commands

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*

interface ICommand {
    val spaceClient: SpaceClient
    val name: String
    val info: String
    suspend fun run(payload: MessagePayload): Unit
    fun toSpaceCommand() = CommandDetail(name, info)
    suspend fun sendMessage(userID: String, message: ChatMessage) = spaceClient.chats.messages.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userID)),
        message
    )
}
