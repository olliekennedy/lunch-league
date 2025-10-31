package com.olliekennedy.playwright

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.olliekennedy.app
import org.http4k.playwright.Http4kBrowser
import org.http4k.playwright.LaunchPlaywrightBrowser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PlaywrightBrowserTests {

    @RegisterExtension
    val playwright = LaunchPlaywrightBrowser(app)

    @Test
    fun `can browse app`(browser: Http4kBrowser) {
        with(browser.newPage()) {
            assertThat(navigateHome().text(), contains("Lunch League".toRegex()))
        }
    }

    @Test
    fun `can navigate from home to vote page`(browser: Http4kBrowser) {
        with(browser.newPage()) {
            navigateHome()
            click("text=Place a vote")
            assertThat(content(), contains("Place your vote:".toRegex()))
        }
    }

    @Test
    fun `submitting vote form redirects to homepage and shows leaderboard`(browser: Http4kBrowser) {
        with(browser.newPage()) {
            setDefaultTimeout(2000.0)

            navigate("/vote")

            fill("input[id=restaurant]", "Sushi Place")
            fill("input[id=rating]", "5")
            fill("input[id=name]", "Ollie")

            click("button[type=submit]")

            assertThat(content(), contains("Lunch League".toRegex()))

            assertThat(content(), contains("Sushi Place".toRegex()))
            assertThat(content(), contains("5.0".toRegex()))
        }
    }

    @Test
    fun `submitting vote form incorrectly returns bad request with feedback`(browser: Http4kBrowser) {
        with(browser.newPage()) {
            setDefaultTimeout(2000.0)

            navigate("/vote")

            fill("input[id=restaurant]", "")
            fill("input[id=rating]", "11")
            fill("input[id=name]", "")

            click("button[type=submit]")

            assertThat(content(), contains("Restaurant is required.".toRegex()))

            assertThat(content(), contains("Name is required.".toRegex()))
            assertThat(content(), contains("Rating must be a number between 1 and 10.".toRegex()))
        }
    }
}

