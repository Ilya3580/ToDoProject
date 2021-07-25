package com.example.todoproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taskAct")
data class TaskAct (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : String,
    @ColumnInfo(name = "act")
    var act : Int
) {
    companion object{
        const val ACT_DELETE = 0
        const val ACT_UPDATE = 1
        const val ACT_ADD = 2
    }
}
