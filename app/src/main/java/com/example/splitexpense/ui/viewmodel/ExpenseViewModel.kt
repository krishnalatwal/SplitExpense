package com.example.splitexpense.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitexpense.data.repository.ExpenseRepository
import com.example.splitexpense.data.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BalanceInfo(
    val memberId: String,
    val memberName: String,
    val balance: Double
)

data class Settlement(
    val from: String,
    val to: String,
    val amount: Double
)

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    val groups: StateFlow<List<Group>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentGroupId = MutableStateFlow<String?>(null)
    val currentGroupId: StateFlow<String?> = _currentGroupId.asStateFlow()

    val currentMembers: StateFlow<List<Member>> = _currentGroupId
        .filterNotNull()
        .flatMapLatest { groupId ->
            repository.getMembersForGroup(groupId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentExpenses: StateFlow<List<Expense>> = _currentGroupId
        .filterNotNull()
        .flatMapLatest { groupId ->
            repository.getExpensesForGroup(groupId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectGroup(groupId: String) {
        _currentGroupId.value = groupId
    }

    fun createGroup(name: String, memberNames: List<String>) {
        viewModelScope.launch {
            repository.createGroup(name, memberNames)
        }
    }

    fun addExpense(
        groupId: String,
        title: String,
        amount: Double,
        paidByMemberId: String,
        participantIds: List<String>,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.addExpense(groupId, title, amount, paidByMemberId, participantIds, date)
        }
    }

    suspend fun calculateBalances(groupId: String): List<BalanceInfo> {
        val members = currentMembers.value
        val expenses = currentExpenses.value

        val balances = members.associate { it.id to BalanceInfo(it.id, it.name, 0.0) }.toMutableMap()

        for (expense in expenses) {
            val participants = repository.getParticipantsForExpense(expense.id)
            val share = expense.amount / participants.size

            participants.forEach { participant ->
                balances[participant.id] = balances[participant.id]!!.copy(
                    balance = balances[participant.id]!!.balance - share
                )
            }

            balances[expense.paidByMemberId] = balances[expense.paidByMemberId]!!.copy(
                balance = balances[expense.paidByMemberId]!!.balance + expense.amount
            )
        }

        return balances.values.toList()
    }

    fun calculateSettlements(balances: List<BalanceInfo>): List<Settlement> {
        val debtors = balances.filter { it.balance < -0.01 }.sortedBy { it.balance }.toMutableList()
        val creditors = balances.filter { it.balance > 0.01 }.sortedByDescending { it.balance }.toMutableList()

        val settlements = mutableListOf<Settlement>()
        var i = 0
        var j = 0

        while (i < debtors.size && j < creditors.size) {
            val debtor = debtors[i]
            val creditor = creditors[j]
            val amount = minOf(-debtor.balance, creditor.balance)

            settlements.add(Settlement(debtor.memberName, creditor.memberName, amount))

            debtors[i] = debtor.copy(balance = debtor.balance + amount)
            creditors[j] = creditor.copy(balance = creditor.balance - amount)

            if (debtors[i].balance >= -0.01) i++
            if (creditors[j].balance <= 0.01) j++
        }

        return settlements
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}