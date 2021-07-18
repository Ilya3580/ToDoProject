package com.example.todoproject.dao

import android.content.Context
import androidx.room.*
import com.example.todoproject.dao.Task
import java.util.*

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
