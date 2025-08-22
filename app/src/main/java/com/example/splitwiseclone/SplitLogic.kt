package com.example.splitwiseclone

import kotlin.math.absoluteValue
import kotlin.math.min

// A data class to represent a single, simplified transaction needed to settle up.
data class Settlement(
    val from: Member,
    val to: Member,
    val amount: Double
)

fun calculateSettlements(group: Group): List<Settlement> {
    // Step 1: Calculate the net balance for each member.
    val balances = mutableMapOf<Int, Double>()
    group.members.forEach { balances[it.id] = 0.0 }

    group.expenses.forEach { expense ->
        val amountPerPerson = expense.amount / expense.splitAmongMemberIds.size

        // The person who paid gets credited.
        balances[expense.paidByMemberId] = balances.getValue(expense.paidByMemberId) + expense.amount

        // Each person in the split gets debited their share.
        expense.splitAmongMemberIds.forEach { memberId ->
            balances[memberId] = balances.getValue(memberId) - amountPerPerson
        }
    }

    // Step 2: Separate members into debtors and creditors.
    val debtors = balances.filter { it.value < -0.01 } // Use a small tolerance for floating point math
    val creditors = balances.filter { it.value > 0.01 }

    val mutableDebtors = debtors.toMutableMap()
    val mutableCreditors = creditors.toMutableMap()
    val settlements = mutableListOf<Settlement>()

    // Step 3: Match debtors to creditors to create settlement transactions.
    while (mutableDebtors.isNotEmpty() && mutableCreditors.isNotEmpty()) {
        val debtorEntry = mutableDebtors.entries.first()
        val creditorEntry = mutableCreditors.entries.first()

        val debtorId = debtorEntry.key
        var debtorAmount = debtorEntry.value
        val creditorId = creditorEntry.key
        var creditorAmount = creditorEntry.value

        val amountToSettle = min(debtorAmount.absoluteValue, creditorAmount)

        val debtorMember = group.members.find { it.id == debtorId }!!
        val creditorMember = group.members.find { it.id == creditorId }!!

        settlements.add(Settlement(from = debtorMember, to = creditorMember, amount = amountToSettle))

        // Update the balances
        mutableDebtors[debtorId] = debtorAmount + amountToSettle
        mutableCreditors[creditorId] = creditorAmount - amountToSettle

        // If a balance is settled (close to zero), remove them from the map.
        if (mutableDebtors.getValue(debtorId).absoluteValue < 0.01) {
            mutableDebtors.remove(debtorId)
        }
        if (mutableCreditors.getValue(creditorId).absoluteValue < 0.01) {
            mutableCreditors.remove(creditorId)
        }
    }

    return settlements
}