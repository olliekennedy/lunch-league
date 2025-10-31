package com.olliekennedy.playwright

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.olliekennedy.app
import org.http4k.playwright.Http4kBrowser
import org.http4k.playwright.LaunchPlaywrightBrowser
import org.junit.jupiter.api.Disabled
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
}

