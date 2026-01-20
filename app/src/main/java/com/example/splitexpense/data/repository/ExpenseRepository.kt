package com.example.splitexpense.data.repository

import com.example.splitexpense.data.dao.AppDao
import com.example.splitexpense.data.entity.Expense
import com.example.splitexpense.data.entity.ExpenseParticipant
import com.example.splitexpense.data.entity.Group
import com.example.splitexpense.data.entity.Member
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: AppDao) {

    fun getAllGroups(): Flow<List<Group>> = dao.getAllGroups()

    fun getMembersForGroup(groupId: String): Flow<List<Member>> =
        dao.getMembersForGroup(groupId)

    fun getExpensesForGroup(groupId: String): Flow<List<Expense>> =
        dao.getExpensesForGroup(groupId)

    suspend fun createGroup(name: String, memberNames: List<String>) {
        val groupId = "group_${System.currentTimeMillis()}"
        val group = Group(
            id = groupId,
            name = name,
            createdAt = System.currentTimeMillis(),
            isPinned = false
        )
        dao.insertGroup(group)

        val members = memberNames.mapIndexed { index, memberName ->
            Member(
                id = "${groupId}_member_$index",
                groupId = groupId,
                name = memberName
            )
        }
        dao.insertMembers(members)
    }

    suspend fun addExpense(
        groupId: String,
        title: String,
        amount: Double,
        paidByMemberId: String,
        participantIds: List<String>,
        date: Long
    ) {
        val expenseId = "expense_${System.currentTimeMillis()}"
        val expense = Expense(
            id = expenseId,
            groupId = groupId,
            title = title,
            amount = amount,
            paidByMemberId = paidByMemberId,
            date = date
        )
        dao.insertExpense(expense)

        val participants = participantIds.map { memberId ->
            ExpenseParticipant(expenseId, memberId)
        }
        dao.insertExpenseParticipants(participants)
    }

    suspend fun getParticipantsForExpense(expenseId: String): List<Member> =
        dao.getMembersForExpense(expenseId)

    suspend fun deleteGroup(group: Group) = dao.deleteGroup(group)

    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)
}