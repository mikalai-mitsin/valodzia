package com.valodzia

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.valodzia.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {
    configureRouting()
}
