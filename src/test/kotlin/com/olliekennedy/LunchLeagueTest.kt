package com.olliekennedy

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.natpryce.hamkrest.equalTo
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.lens.contentType
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class LunchLeagueTest {

    @Test
    fun `home shows the leaderboard`() {
        val response = app(Request(Method.GET, "/"))
        assertThat(Status.OK, equalTo(response.status))
        assertThat(response.contentType(), equalTo(TEXT_HTML))
        val body = response.bodyString()
        assertThat(body, contains("<h1>Lunch League</h1>".toRegex()))
        assertThat(body, contains("<h2>Leaderboard:</h2>".toRegex()))
    }

    @Test
    fun `vote shows a voting page`() {
        val response = app(Request(Method.GET, "/vote"))
        assertThat(Status.OK, equalTo(response.status))
        assertThat(response.contentType(), equalTo(TEXT_HTML))
        val body = response.bodyString()
        assertThat(body, contains("<h1>Lunch League</h1>".toRegex()))
        assertThat(body, contains("<h2>Place your vote:</h2>".toRegex()))
    }

    @Test
    fun `vote page has a voting form`() {
        val response = app(Request(Method.GET, "/vote"))
        val doc = Jsoup.parse(response.bodyString())
        val form = doc.selectFirst("form")
        assertNotNull(form)

        assertNotNull(form.selectFirst("input[id=restaurant]"))
        assertNotNull(form.selectFirst("input[id=rating]"))
        assertNotNull(form.selectFirst("input[id=name]"))

        val submit = form.selectFirst("button[type=submit]")
        assertNotNull(submit)
    }

    @Test
    fun `home page contains a vote button`() {
        val response = app(Request(Method.GET, "/"))
        val doc = Jsoup.parse(response.bodyString())
        val voteLink = doc.select("a[href=/vote]").first()
        assertThat(voteLink == null, equalTo(false))
    }

    @Test
    fun `submitting a vote updates leaderboard and redirects home`() {
        val response = app(
            Request(Method.POST, "/vote")
                .form("restaurant", "Sushi Place")
                .form("rating", "5")
                .form("name", "Ollie")
        )

        assertThat(response.status, equalTo(Status.FOUND))
        assertThat(response.header("Location"), equalTo("/"))

        val home = app(Request(Method.GET, "/")).bodyString()
        assertThat(home, contains("Sushi Place".toRegex()))
        assertThat(home, contains("5.0".toRegex()))
    }
}
