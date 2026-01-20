package com.example.splitexpense.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "expense_participants",
    primaryKeys = ["expenseId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = Expense::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseParticipant(
    val expenseId: String,
    val memberId: String
)