package com.`018bf`

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.`018bf`.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {
    configureRouting()
}
