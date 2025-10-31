package com.olliekennedy

import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
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

object VotePage : ViewModel
data class HomePage(val leaderboard: String) : ViewModel

val renderer = HandlebarsTemplates().CachingClasspath()
val view = Body.viewModel(renderer, TEXT_HTML).toLens()

val voteManager = VoteManager()

val app = routes(
    "/" bind Method.GET to {
        Response(Status.OK).with(view of HomePage(voteManager.leaderboard()))
    },

    "/vote" bind Method.GET to {
        Response(Status.OK).with(view of VotePage)
    },

    "/vote" bind Method.POST to { req ->
        val restaurant = req.form("restaurant") ?: ""
        val rating = req.form("rating")?.toIntOrNull() ?: 0
        val name = req.form("name") ?: ""
        voteManager.vote(restaurant, name, rating)
        Response(Status.FOUND).header("Location", "/")
    }
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
