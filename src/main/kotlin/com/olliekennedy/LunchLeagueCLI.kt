package com.olliekennedy

fun main() {
    val voteManager = VoteManager()

    println("Welcome to Lunch League!")
    while (true) {
        println("\nChoose an option:")
        println("1. Vote")
        println("2. Show results")
        println("3. Show voting log for a restaurant")
        println("4. Exit")
        when (readLine()?.trim()) {
            "1" -> {
                print("Restaurant name: ")
                val restaurant = readLine()?.trim().orEmpty()
                print("Your name: ")
                val voter = readLine()?.trim().orEmpty()
                print("Your rating (1-10): ")
                val rating = readLine()?.trim()?.toIntOrNull() ?: 0
                voteManager.vote(restaurant, voter, rating)
                println("Vote recorded.")
            }
            "2" -> {
                println("Leaderboard:")
                println(voteManager.leaderboard())
            }
            "3" -> {
                print("Restaurant name: ")
                val restaurant = readLine()?.trim().orEmpty()
                val log = voteManager.votingLogFor(restaurant)
                if (log.isEmpty()) {
                    println("No votes for $restaurant.")
                } else {
                    log.forEach { println("${it.voter}: ${it.rating}") }
                }
            }
            "4" -> {
                println("Goodbye!")
                break
            }
            else -> println("Invalid option.")
        }
    }
}