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

data class VotePage(val errors: List<String> = emptyList()) : ViewModel
data class HomePage(val leaderboard: String) : ViewModel

val renderer = HandlebarsTemplates().CachingClasspath()
val view = Body.viewModel(renderer, TEXT_HTML).toLens()

val voteManager = VoteManager()

val app = routes(
    "/" bind Method.GET to {
        Response(Status.OK).with(view of HomePage(voteManager.leaderboard()))
    },

    "/vote" bind Method.GET to {
        Response(Status.OK).with(view of VotePage())
    },

    "/vote" bind Method.POST to { req ->
        val restaurant = req.form("restaurant")?.trim().orEmpty()
        val rating = req.form("rating")?.toIntOrNull()
        val name = req.form("name")?.trim().orEmpty()

        val errors = mutableListOf<String>()
        if (restaurant.isEmpty()) errors += "Restaurant is required."
        if (name.isEmpty()) errors += "Name is required."
        if (rating == null || rating !in 1..10) errors += "Rating must be a number between 1 and 10."

        if (errors.isNotEmpty()) {
            Response(Status.OK).with(view of VotePage(errors))
        } else {
            voteManager.vote(restaurant, name, rating!!)
            Response(Status.FOUND).header("Location", "/")
        }
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
