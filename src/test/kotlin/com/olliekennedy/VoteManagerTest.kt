package com.olliekennedy

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class VoteManagerTest {
    val voteManager = VoteManager()

    @Test
    fun `can calculate average ratings for each restaurant`() {
        placeABunchOfVotes()

        assertThat(
            voteManager.scores(),
            equalTo(
                listOf(
                    Entry("Canteen", 6.3),
                    Entry("Randy's Wing Bar", 2.5),
                    Entry("Hackney Bridge", 6.5),
                )
            )
        )
    }

    @Test
    fun `can get a voting log for a restaurant`() {
        placeABunchOfVotes()

        assertThat(
            voteManager.votingLogFor("Canteen"),
            List<Vote>::containsAll,
            listOf(Vote("me", 1), Vote("bob", 10), Vote("sandra", 8), Vote("brenda", 6)),
        )
        assertThat(
            voteManager.votingLogFor("Canteen"),
            List<Vote>::containsAll,
            listOf(Vote("me", 1), Vote("bob", 10), Vote("sandra", 8), Vote("brenda", 6)),
        )
        assertThat(
            voteManager.votingLogFor("Canteen"),
            List<Vote>::containsAll,
            listOf(Vote("me", 1), Vote("bob", 10), Vote("sandra", 8), Vote("brenda", 6)),
        )
    }

    @Test
    fun `someone can update their rating for a restaurant`() {
        voteManager.vote("Canteen", "me", 6)
        voteManager.vote("Canteen", "me", 8)

        assertThat(voteManager.votingLogFor("Canteen"), equalTo(listOf(Vote("me", 8))))
    }

    @Test
    fun `can get a nice leaderboard`() {
        placeABunchOfVotes()

        assertThat(
            voteManager.leaderboard(),
            equalTo(
                """
                    1. Hackney Bridge 6.5
                    2. Canteen 6.3
                    3. Randy's Wing Bar 2.5
                """.trimIndent()
            )
        )
    }

    private fun placeABunchOfVotes() {
        voteManager.vote("Canteen", "me", 1)
        voteManager.vote("Canteen", "bob", 10)
        voteManager.vote("Canteen", "sandra", 8)
        voteManager.vote("Canteen", "brenda", 6)
        voteManager.vote("Randy's Wing Bar", "me", 1)
        voteManager.vote("Randy's Wing Bar", "bob", 2)
        voteManager.vote("Randy's Wing Bar", "sandra", 3)
        voteManager.vote("Randy's Wing Bar", "brenda", 4)
        voteManager.vote("Hackney Bridge", "me", 3)
        voteManager.vote("Hackney Bridge", "bob", 4)
        voteManager.vote("Hackney Bridge", "sandra", 9)
        voteManager.vote("Hackney Bridge", "brenda", 10)
    }
}
