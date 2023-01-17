package com.u018bf

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.u018bf.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {
    configureRouting()
}
