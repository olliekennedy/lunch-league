package com.olliekennedy

import java.math.BigDecimal
import java.math.RoundingMode

class VoteManager() {
    val votes = mutableMapOf<String, MutableMap<String, Int>>()

    fun vote(restaurant: String, voter: String, rating: Int) {
        val restaurantVotes = votes.getOrPut(restaurant) { mutableMapOf() }
        restaurantVotes[voter] = rating
    }

    fun scores(): List<Entry> =
        votes.entries.map { (name, votes) ->
            val rating = votes.map { it.value }.average()
            Entry(name, rating)
        }.toList()

    fun votingLogFor(restaurantName: String): List<Vote> =
        votes
            .getOrElse(restaurantName) { mapOf() }
            .map { Vote(it.key, it.value) }

    fun leaderboard(): String =
        scores()
            .sortedByDescending { it.rating }
            .mapIndexed { i, it -> "${i + 1}. ${it.name} ${it.rating}" }
            .joinToString("\n")
}

private fun List<Int>.average(): Double = sum().div(size.toDouble()).roundedToOneDecimalPlace()
private fun Double.roundedToOneDecimalPlace(): Double = BigDecimal(this).setScale(1, RoundingMode.HALF_UP).toDouble()

data class Entry(val name: String, val rating: Double)
data class Vote(val voter: String, val rating: Int)