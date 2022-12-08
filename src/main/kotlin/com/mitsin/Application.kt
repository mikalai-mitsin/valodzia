package com.mitsin

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.mitsin.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {
    configureRouting()
}
