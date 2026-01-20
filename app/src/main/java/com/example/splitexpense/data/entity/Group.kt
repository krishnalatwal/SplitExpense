package com.example.splitexpense.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
    val isPinned: Boolean = false
)