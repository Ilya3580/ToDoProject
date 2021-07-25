package com.example.todoproject.database

import androidx.room.*

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    @Update
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)

    @Query("select * from task where id = :id")
    suspend fun findTask(id: String): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskAct(task: TaskAct)
    @Update
    suspend fun updateTaskAct(task: TaskAct)
    @Delete
    suspend fun deleteTaskAct(task: TaskAct)

    @Query("select * from taskAct where id = :id")
    suspend fun findTaskAct(id : String) : TaskAct

    @Query("select * from taskAct")
    suspend fun listTaskAct() : List<TaskAct>

    @Query("select * from task")
    suspend fun listTask() : List<Task>


}