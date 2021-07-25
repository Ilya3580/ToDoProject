package com.example.todoproject.database

import android.content.Context
import androidx.room.*

@Database(
    version = 1,
    entities = [Task::class, TaskAct::class]
)
abstract class DatabaseStorage : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val name = "task.bd"
        fun build(context: Context): DatabaseStorage {
            return Room.databaseBuilder(
                context,
                DatabaseStorage::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
