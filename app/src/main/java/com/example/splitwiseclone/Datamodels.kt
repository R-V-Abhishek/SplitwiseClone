package com.example.splitwiseclone

data class Member(
    val id: Int,
    val name: String
)

data class Expense(
    val id: Int,
    val description: String,
    val amount: Double,
    val paidByMemberId: Int, // The ID of the member who paid.
    val splitAmongMemberIds: List<Int> // The IDs of members this is split between.
)

data class Group(
    val id: Int,
    val name: String,
    val members: List<Member>,
    val expenses: List<Expense>
)


val sampleMembers = listOf(
    Member(id = 1, name = "Hemanth"),
    Member(id = 2, name = "Shreekar"),
    Member(id = 3, name = "Vedantha")
)

// A list of sample groups for initial display
val sampleGroups = listOf(
    Group(
        id = 1,
        name = "Absolute Brilliance",
        members = sampleMembers,
        expenses = listOf(
            Expense(
                id = 101,
                description = "Lunch",
                amount = 1200.0,
                paidByMemberId = 1, // Alice paid
                splitAmongMemberIds = listOf(1, 2, 3) // Split among Alice, Bob, and Charlie
            )
        )
    ),
    Group(
        id = 2,
        name = "Hampi Trip",
        members = sampleMembers.take(2), // Just Alice and Bob
        expenses = listOf()
    )
)