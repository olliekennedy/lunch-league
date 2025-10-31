package com.olliekennedy

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.lens.html
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

val app = routes(
    "/" bind Method.GET to {
        Response(Status.OK).html(
            """
                <h1>Lunch League</h1>
            """.trimIndent())
    },

    "/vote" bind Method.GET to {
        Response(Status.OK).html(
            """
                <h1>Lunch League</h1>
                <h2>Place your vote:</h2>
            """.trimIndent()
        )
    },
)

fun buildApp(debug: Boolean = false): HttpHandler {
    val core = SecurityHeaders.Add.then(app)
    return if (debug) PrintRequest().then(core) else core
}

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 9000
    buildApp(debug = true).asServer(Jetty(port)).start()
    println("Server started on $port")
}
