package com.olliekennedy

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.lens.contentType
import org.junit.jupiter.api.Test

class LunchLeagueTest {

    @Test
    fun `home shows the leaderboard`() {
        val response = app(Request(Method.GET, "/"))
        assertThat(Status.OK, equalTo(response.status))
        assertThat(response.contentType(), equalTo(TEXT_HTML))
        val title = response.bodyString()
        assertThat(title, equalTo("Lunch League"))
    }

    @Test
    fun `vote shows a voting page`() {
        val response = app(Request(Method.GET, "/vote"))
        assertThat(Status.OK, equalTo(response.status))
        assertThat(response.contentType(), equalTo(TEXT_HTML))
        val title = response.bodyString()
        assertThat(title, equalTo("Place your vote:"))
    }
}
