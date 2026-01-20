package com.example.splitexpense.data.dao

import androidx.room.*
import com.example.splitexpense.data.entity.Expense
import com.example.splitexpense.data.entity.ExpenseParticipant
import com.example.splitexpense.data.entity.Group
import com.example.splitexpense.data.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Groups
    @Query("SELECT * FROM groups ORDER BY isPinned DESC, createdAt DESC")
    fun getAllGroups(): Flow<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroup(groupId: String): Group?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    // Members
    @Query("SELECT * FROM members WHERE groupId = :groupId")
    fun getMembersForGroup(groupId: String): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<Member>)

    // Expenses
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC")
    fun getExpensesForGroup(groupId: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // Expense Participants
    @Query("SELECT * FROM expense_participants WHERE expenseId = :expenseId")
    suspend fun getParticipantsForExpense(expenseId: String): List<ExpenseParticipant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseParticipants(participants: List<ExpenseParticipant>)

    @Query("SELECT * FROM members WHERE id IN (SELECT memberId FROM expense_participants WHERE expenseId = :expenseId)")
    suspend fun getMembersForExpense(expenseId: String): List<Member>
}