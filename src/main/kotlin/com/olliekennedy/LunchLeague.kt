package com.olliekennedy

import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

object HomePage : ViewModel
object VotePage : ViewModel

val renderer = HandlebarsTemplates().CachingClasspath()
val view = Body.viewModel(renderer, TEXT_HTML).toLens()

val app = routes(
    "/" bind Method.GET to {
        Response(Status.OK).with(view of HomePage)
    },

    "/vote" bind Method.GET to {
        Response(Status.OK).with(view of VotePage)
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
