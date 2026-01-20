package com.example.splitexpense.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.splitexpense.data.dao.AppDao
import com.example.splitexpense.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Group::class, Member::class, Expense::class, ExpenseParticipant::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "split_expense_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    initializePersonalGroup(database.appDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun initializePersonalGroup(dao: AppDao) {
            val personalGroup = Group(
                id = "personal",
                name = "Personal",
                createdAt = System.currentTimeMillis(),
                isPinned = true
            )
            val personalMember = Member(
                id = "you",
                groupId = "personal",
                name = "You"
            )
            dao.insertGroup(personalGroup)
            dao.insertMember(personalMember)
        }
    }
}